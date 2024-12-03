package ru.yandex.practicum;

import lombok.*;

import static ru.yandex.practicum.SensorEventType.MOTION_SENSOR_EVENT;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class MotionSensorEvent extends SensorEvent {

    private int linkQuality;
    private boolean motion;
    private int voltage;

    @Override
    public SensorEventType getType() {
        return MOTION_SENSOR_EVENT;
    }
}
