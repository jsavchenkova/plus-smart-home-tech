package ru.practicum.serialize;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorsSnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro>{
    public SensorsSnapshotDeserializer(){
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
