package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.contant.DeviceSensorType;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.DEVICE_REMOVED;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler{
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return DEVICE_REMOVED;
    }

    private final CollectorService service;

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Устройство добавлено");
        DeviceRemovedEventProto deviceRemoved = event.getDeviceRemoved();

        DeviceRemovedEvent deviceEvent = new DeviceRemovedEvent();
        deviceEvent.setHubId(event.getHubId());
        deviceEvent.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));
        deviceEvent.setId(event.getDeviceAdded().getId());

        service.processingHub(deviceEvent);

    }
}
