package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Service
public class HubService {
    public void processingEvent (DeviceAddedEvent event){

    }

    public void processingEvent (DeviceRemovedEvent event){

    }

    public void processingEvent (ScenarioAddedEvent event){}

    public void processingEvent (ScenarioRemovedEvent event){}
}
