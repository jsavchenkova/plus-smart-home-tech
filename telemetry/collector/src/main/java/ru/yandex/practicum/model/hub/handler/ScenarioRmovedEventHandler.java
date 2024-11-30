package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.SCENARIO_REMOVED;

@Component
@RequiredArgsConstructor
public class ScenarioRmovedEventHandler implements HubEventHandler{
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return SCENARIO_REMOVED;
    }

    private final CollectorService service;

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Сценарий удалён");

        ScenarioRemovedEventProto eventProto = event.getScenarioRemoved();

        ScenarioRemovedEvent scenario = new ScenarioRemovedEvent();
        scenario.setHubId(event.getHubId());
        scenario.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds()));

        service.processingHub(scenario);
    }
}
