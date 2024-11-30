package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "scenario_actions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionCompositeKey key;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;
}
