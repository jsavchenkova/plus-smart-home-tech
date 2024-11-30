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
import ru.yandex.practicum.serialize.hub.HubEventSerializer;
import ru.yandex.practicum.serialize.sensor.SensorAvroSerializer;

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

        sendSensorEvent(eventAvro);

    }

    public void processingSensors(LightSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new LightSensorAvro(sensorEvent.getLinkQuality(), sensorEvent.getLuminosity()));
        sendSensorEvent(eventAvro);
    }

    public void processingSensors(MotionSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new MotionSensorAvro
                (sensorEvent.getLinkQuality(), sensorEvent.isMotion(), sensorEvent.getVoltage()));

        sendSensorEvent(eventAvro);

    }

    public void processingSensors(SwitchSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new SwitchSensorAvro(sensorEvent.isState()));

        sendSensorEvent(eventAvro);

    }

    public void processingSensors(TemperatureSensorEvent sensorEvent) {
        SensorEventAvro eventAvro = new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
                , new TemperatureSensorAvro (sensorEvent.getTemperatureC(), sensorEvent.getTemperatureF()));

        sendSensorEvent(eventAvro);
    }

    public void processingHub(DeviceAddedEvent hubEvent) {
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name());
        HubEventAvro eventAvro = new HubEventAvro(hubEvent.getHubId(), hubEvent.getTimestamp(),
                new DeviceAddedEventAvro(hubEvent.getId(), deviceTypeAvro));

        sendHubEvent(eventAvro);

    }

    public void processingHub(DeviceRemovedEvent hubEvent) {
        HubEventAvro eventAvro = new HubEventAvro(hubEvent.getHubId(), hubEvent.getTimestamp(),
                new DeviceRemovedEventAvro(hubEvent.getId()));

        sendHubEvent(eventAvro);

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
        HubEventAvro eventAvro = new HubEventAvro(hubEvent.getHubId(), hubEvent.getTimestamp(), event);

        sendHubEvent(eventAvro);

    }

    public void processingHub(ScenarioRemovedEvent hubEvent) {
        HubEventAvro eventAvro = new HubEventAvro(hubEvent.getHubId(), hubEvent.getTimestamp(),
        new ScenarioRemovedEventAvro(hubEvent.getName()));

        sendHubEvent(eventAvro);

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

    private void sendSensorEvent(SensorEventAvro eventAvro) {
        Properties config = getPropertiesSensor();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorAvroSerializer.class);

        Producer<String, SensorEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(topic, eventAvro);

        producer.send(record);
        producer.close();
    }

    private void sendHubEvent (HubEventAvro eventArvo){
        Properties config = getPropertiesHub();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, HubEventSerializer.class);

        Producer<String, HubEventAvro> producer = new KafkaProducer<>(config);
        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, HubEventAvro> record = new ProducerRecord<>(topic, eventArvo);

        producer.send(record);
        producer.close();
    }

}
