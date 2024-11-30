package ru.yandex.practicum.mapper;

import ru.yandex.practicum.contant.ActionType;
import ru.yandex.practicum.contant.ConditionOperation;
import ru.yandex.practicum.contant.ConditionType;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.model.hub.DeviceActionEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioCondition;

import java.time.Instant;
import java.util.List;

public class ScenarioAddedEventMapper {

    public static ScenarioAddedEvent mapHubEventProtoToScenarioAddedEvent(HubEventProto hubeventProto) {

        ScenarioAddedEvent scenario = new ScenarioAddedEvent();
        scenario.setHubId(hubeventProto.getHubId());
        scenario.setTimestamp(Instant.ofEpochSecond(hubeventProto.getTimestamp().getSeconds()));
        scenario.setName(hubeventProto.getScenarioAdded().getName());
        List<ScenarioCondition> conditions = hubeventProto.getScenarioAdded().getConditionsList().stream()
                .map(x -> new ScenarioCondition(x.getSensorId(), ConditionType.valueOf(x.getType().name()),
                        ConditionOperation.valueOf(x.getOperation().name()), x.getIntValue()))
                .toList();
        scenario.setConditions(conditions);
        List<DeviceActionEvent> actions = hubeventProto.getScenarioAdded().getActionsList().stream()
                .map(x -> new DeviceActionEvent(x.getSensorId(), ActionType.valueOf(x.getType().name()), x.getValue()))
                .toList();
        scenario.setActions(actions);
        return scenario;
    }
}


