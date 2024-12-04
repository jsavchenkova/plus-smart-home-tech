package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.model.SensorEventSnapshot;
import ru.practicum.model.SensorState;
import ru.practicum.serialize.SensorsSnapshotDeserializer;
import ru.practicum.service.SnapshotService;
import ru.yandex.practicum.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;
    @Value("${kafka.client-id}")
    private String clientId;
    @Value("${kafka.group-id}")
    private String groupId;

    private static final List<String> topics = List.of("telemetry.snapshots.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final SnapshotService service;

    public void start() {
        Properties config = getPropertiesConsumerSnapshot();
        KafkaConsumer<Void, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(config);

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {

            consumer.subscribe(topics);

            while (true) {

                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(consume_attempt_timeout);
                int count = 0;
                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {

                    System.out.println("Получено сообщение. topic: telemetry.snapshots.v1");
                    System.out.println(record.value());

                    SensorEventSnapshot snapshot = new SensorEventSnapshot();
                    snapshot.setHubId(record.value().getHubId());
                    snapshot.setTimestapm(record.value().getTimestamp());
                    Map<String, SensorState> stateMap = new HashMap<>();
                    Set<String> keys = record.value().getSensorsState().keySet();
                    for (String str : keys) {
                        SensorStateAvro stateAvro = record.value().getSensorsState().get(str);
                        SensorState state = new SensorState();
                        state.setTimestamp(stateAvro.getTimestamp());
                        SensorEvent sensorEvent = null;
                        switch (stateAvro.getData().getClass().getSimpleName()) {
                            case "MotionSensorAvro":
                                sensorEvent = new MotionSensorEvent(
                                        ((MotionSensorAvro) stateAvro.getData()).getLinkQuality(),
                                        ((MotionSensorAvro) stateAvro.getData()).getMotion(),
                                        ((MotionSensorAvro) stateAvro.getData()).getVoltage()
                                );
                                break;
                            case "TemperatureSensorAvro":
                                sensorEvent = new TemperatureSensorEvent(
                                        ((TemperatureSensorAvro) stateAvro.getData()).getTemperatureC(),
                                        ((TemperatureSensorAvro) stateAvro.getData()).getTemperatureF()
                                );
                                break;
                            case "LightSensorAvro":
                                sensorEvent = new LightSensorEvent(
                                        ((LightSensorAvro) stateAvro.getData()).getLinkQuality(),
                                        ((LightSensorAvro) stateAvro.getData()).getLuminosity()
                                );
                                break;
                            case "ClimateSensorAvro":
                                sensorEvent = new ClimateSensorEvent(
                                        ((ClimateSensorAvro) stateAvro.getData()).getTemperatureC(),
                                        ((ClimateSensorAvro) stateAvro.getData()).getHumidity(),
                                        ((ClimateSensorAvro) stateAvro.getData()).getCo2Level()
                                );
                                break;
                            case "SwitchSensorAvro":
                                sensorEvent = new SwitchSensorEvent(((SwitchSensorAvro) stateAvro.getData()).getState());
                                break;
                        }
                        if (sensorEvent == null) continue;
                        sensorEvent.setHubId(record.value().getHubId());
                        sensorEvent.setId(str);
                        sensorEvent.setTimestamp(stateAvro.getTimestamp());
                        state.setData(sensorEvent);

                        stateMap.put(str, state);
                    }
                    snapshot.setSensorsState(stateMap);
                    service.processingSnapshot(snapshot);

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();

            }

        } catch (
                WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (
                Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }


    }

    private Properties getPropertiesConsumerSnapshot() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return config;
    }

    private static void manageOffsets(ConsumerRecord<Void, SensorsSnapshotAvro> record, int count, KafkaConsumer<Void, SensorsSnapshotAvro> consumer) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}


