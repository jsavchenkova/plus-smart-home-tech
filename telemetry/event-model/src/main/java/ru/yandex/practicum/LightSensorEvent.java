package ru.yandex.practicum;

import lombok.*;

import static ru.yandex.practicum.SensorEventType.LIGHT_SENSOR_EVENT;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LightSensorEvent extends SensorEvent {

    private int linkQuality;
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return LIGHT_SENSOR_EVENT;
    }
}
