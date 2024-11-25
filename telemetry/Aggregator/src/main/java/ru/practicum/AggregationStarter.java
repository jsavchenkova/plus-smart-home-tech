package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.serialize.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.*;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    // ... объявление полей и конструктора ...
    private static final List<String> topics = List.of("telemetry.sensors.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private static final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        Properties config = getPropertiesSensor();
        KafkaConsumer<Void, SensorEventAvro> consumer = new KafkaConsumer<>(config);
        // регистрируем хук, в котором вызываем метод wakeup.
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {

            // ... подготовка к обработке данных ...
            // ... например, подписка на топик ...
            consumer.subscribe(topics);

            // Цикл обработки событий
            while (true) {
                ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(consume_attempt_timeout);

                int count = 0;
                for (ConsumerRecord<Void, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> result = Optional.empty();
                    // обрабатываем очередную запись
                    if(snapshots.containsKey(record.value().getHubId())){
                        SensorsSnapshotAvro snapshot = snapshots.get(record.value().getHubId());
                        if(snapshot.getSensorsState().containsKey(record.value().getId())){
                            if(!snapshot.getSensorsState().get(record.value().getId()).equals(record.value().getPayload())){

                                snapshot.getSensorsState().put(record.value().getId(), (SensorStateAvro) record.value().getPayload());
                                result = Optional.of(snapshot);
                            }
                        } else{
                            snapshot.getSensorsState().put(record.value().getId(), (SensorStateAvro)record.value().getPayload());
                            result = Optional.of(snapshot);
                        }

                    }else{
                        SensorsSnapshotAvro snapshot =  new SensorsSnapshotAvro();
                        snapshot.setHubId(record.value().getHubId());

                        snapshot.setSensorsState(new HashMap<>());
                        snapshot.getSensorsState().put(record.value().getId(), (SensorStateAvro) record.value().getPayload());
                        snapshots.put(record.value().getHubId(), snapshot);
                        result = Optional.of(snapshot);
                    }


                    // фиксируем оффсеты обработанных записей, если нужно
                    manageOffsets(record, count, consumer);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
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
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id");
        return config;
    }

    private static void manageOffsets(ConsumerRecord<Void, SensorEventAvro> record, int count, KafkaConsumer<Void, SensorEventAvro> consumer) {
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
