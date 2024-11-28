package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.serialize.hub.DeviceAddedAvroSerializer;
import ru.yandex.practicum.serialize.hub.DeviceRemovedAvroSerializer;
import ru.yandex.practicum.serialize.sensor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorService {

    public void processingSensors(ClimateSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new ClimateSensorAvro(sensorEvent.getTemperatureC(), sensorEvent.getHumidity(), sensorEvent.getCo2Level()));

        send(eventAvro);

    }

    public void processingSensors(LightSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new LightSensorAvro(sensorEvent.getLinkQuality(), sensorEvent.getLuminosity()));
        send(eventAvro);
    }

    public void processingSensors(MotionSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new MotionSensorAvro
                (sensorEvent.getLinkQuality(), sensorEvent.isMotion(), sensorEvent.getVoltage()));

        send(eventAvro);

    }

    public void processingSensors(SwitchSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new SwitchSensorAvro(sensorEvent.isState()));

        send(eventAvro);

    }

    public void processingSensors(TemperatureSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new TemperatureSensorAvro (sensorEvent.getTemperatureC(), sensorEvent.getTemperatureF()));

        send(eventAvro);
    }

    public void processingHub(DeviceAddedEvent hubEvent) {
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name());
        DeviceAddedEventAvro event = new DeviceAddedEventAvro(hubEvent.getId(), deviceTypeAvro);

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceAddedAvroSerializer.class);

        Producer<String, DeviceAddedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, DeviceAddedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(DeviceRemovedEvent hubEvent) {
        DeviceRemovedEventAvro event = new DeviceRemovedEventAvro(hubEvent.getId());

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, DeviceRemovedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, DeviceRemovedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(ScenarioAddedEvent hubEvent) {
        List<ScenarioConditionAvro> conditionAvros = new ArrayList<>();
        for (ScenarioCondition sc : hubEvent.getConditions()) {
            conditionAvros.add(new ScenarioConditionAvro(sc.getSensorId()
                    , DeviceTypeAvro.valueOf(sc.getType().name())
                    , ConditionOperationAvro.valueOf(sc.getConditionOperation().name())
                    , sc.getValue()));
        }
        List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();
        for (DeviceActionEvent da : hubEvent.getActions()) {
            deviceActionAvros.add(new DeviceActionAvro(da.getSensorId(), ActionTypeAvro.valueOf(da.getType().name()),
                    da.getValue()));
        }

        ScenarioAddedEventAvro event = new ScenarioAddedEventAvro(hubEvent.getName(), conditionAvros, deviceActionAvros);

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, ScenarioAddedEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, ScenarioAddedEventAvro> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    public void processingHub(ScenarioRemovedEvent hubEvent) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();

        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DeviceRemovedAvroSerializer.class);

        Producer<String, ScenarioRemovedEvent> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, ScenarioRemovedEvent> record = new ProducerRecord<>(topic, event);

        producer.send(record);
        producer.close();

    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        return config;
    }

    private Properties getPropertiesHub() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorAvroSerializer.class);
        return config;
    }

    private void send(SensorEventAvro eventAvro) {
        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorAvroSerializer.class);

        Producer<String, SensorEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(topic, eventAvro);

        producer.send(record);
        producer.close();
    }

}
