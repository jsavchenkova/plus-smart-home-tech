package ru.yandex.practicum.model.hub;

import lombok.*;
import ru.yandex.practicum.contant.ActionType;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class DeviceActionEvent {
    private String sensorId;
    private ActionType type;
    private int value;
}
