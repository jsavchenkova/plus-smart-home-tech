package ru.practicum.model;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "scenario_conditions")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioCondition {
    @EmbeddedId
    private ScenarioConditionCompositeKey key;

    @ManyToOne
    @JoinColumn(name="scenario_id")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name="sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name="condition_id")
    private Condition condition;
}
