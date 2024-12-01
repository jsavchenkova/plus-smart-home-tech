package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.ScenarioAddedMapper;
import ru.practicum.serialize.HubEventDeserializer;
import ru.practicum.service.HubService;
import ru.yandex.practicum.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable{
    private static final List<String> topics = List.of("telemetry.hubs.v1");
    private static final Duration consume_attempt_timeout = Duration.ofMillis(1000);
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final HubService service;

    @Override
    public void run(){
        Properties config = getPropertiesConsumerHub();
        KafkaConsumer<Void, HubEventAvro> consumer = new KafkaConsumer<>(config);

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try{

            consumer.subscribe(topics);

            while(true){
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(consume_attempt_timeout);
                int count = 0;
                for (ConsumerRecord<Void, HubEventAvro> record : records) {
                    System.out.println("Получено сообщение. topic: telemetry.hubs.v1" );
                    System.out.println(record.value());

                    String type = record.value().getPayload().getClass().getSimpleName();
                    try {


                        switch (record.value().getPayload().getClass().getSimpleName()) {
                            case "ScenarioRemovedEventAvro":
                                ScenarioRemovedEvent removedEvent = new ScenarioRemovedEvent();
                                removedEvent.setHubId(record.value().getHubId());
                                removedEvent.setTimestamp(record.value().getTimestamp());
                                removedEvent.setName(((ScenarioRemovedEventAvro) record.value().getPayload()).getName());
                                service.processingEvent(removedEvent);
                                break;
                            case "ScenarioAddedEventAvro":
                                ScenarioAddedEvent event = ScenarioAddedMapper.mapScenarioAddedAvroToScenarioAddedEvent(record.value());
                                service.processingEvent(event);
                                break;
                            case "DeviceRemovedEventAvro":
                                DeviceRemovedEvent deviceRemoved = new DeviceRemovedEvent();
                                deviceRemoved.setHubId(record.value().getHubId());
                                deviceRemoved.setTimestamp(record.value().getTimestamp());
                                deviceRemoved.setId(((DeviceRemovedEventAvro) record.value().getPayload()).getId());
                                service.processingEvent(deviceRemoved);
                                break;
                            case "DeviceAddedEventAvro":
                                DeviceAddedEvent deviceAdded = new DeviceAddedEvent();
                                deviceAdded.setHubId(record.value().getHubId());
                                deviceAdded.setTimestamp(record.value().getTimestamp());
                                deviceAdded.setId(((DeviceAddedEventAvro) record.value().getPayload()).getId());
                                deviceAdded.setDeviceType(DeviceSensorType.valueOf(((DeviceAddedEventAvro) record.value().getPayload()).getType().name()));
                                service.processingEvent(deviceAdded);
                                break;
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }


                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        }catch (
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

    private Properties getPropertiesConsumerHub() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "SomeConsumer2");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "some.group.id2");
        return config;
    }

    private static void manageOffsets(ConsumerRecord<Void, HubEventAvro> record, int count, KafkaConsumer<Void, HubEventAvro> consumer) {
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
