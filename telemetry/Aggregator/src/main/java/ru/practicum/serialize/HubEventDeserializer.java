package ru.practicum.serialize;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro>{
    public HubEventDeserializer(){
        super(HubEventAvro.getClassSchema());
    }
}

