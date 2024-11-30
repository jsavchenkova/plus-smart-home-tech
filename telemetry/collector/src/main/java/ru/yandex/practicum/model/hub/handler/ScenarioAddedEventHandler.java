package ru.yandex.practicum.model.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.mapper.ScenarioAddedEventMapper;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioCondition;
import ru.yandex.practicum.service.CollectorService;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase.SCENARIO_ADDED;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler{
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return SCENARIO_ADDED;
    }

    private final CollectorService service;

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Сценарий добавлен");

        ScenarioAddedEvent scenario = ScenarioAddedEventMapper.mapHubEventProtoToScenarioAddedEvent(event);

        service.processingHub(scenario);

    }
}
