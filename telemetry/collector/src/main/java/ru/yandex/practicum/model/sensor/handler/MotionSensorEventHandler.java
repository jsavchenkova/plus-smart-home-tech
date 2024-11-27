package ru.yandex.practicum.model.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase.MOTION_SENSOR;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return MOTION_SENSOR;
    }

    private final CollectorService service;

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Получено событие датчика движения");
        // получаем данные датчика движения
        MotionSensorProto motionSensor = event.getMotionSensor();
        System.out.println("Наличие движения: " + motionSensor.getMotion());
        MotionSensorEvent mevent = new MotionSensorEvent();
        mevent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
        mevent.setId(event.getId());
        mevent.setHubId(event.getHubId());
        mevent.setMotion(event.getMotionSensor().getMotion());
        mevent.setVoltage(event.getMotionSensor().getVoltage());
        mevent.setLinkQuality(event.getMotionSensor().getLinkQuality());


        service.processingSensors( mevent);
    }
}
