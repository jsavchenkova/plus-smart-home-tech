package ru.yandex.practicum.model.hub;

import lombok.*;
import ru.yandex.practicum.contant.ConditionType;
import ru.yandex.practicum.contant.ConditionOperation;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ScenarioCondition {

    private String sensorId;
    private ConditionType type;
    private ConditionOperation conditionOperation;
    private int value;
}
