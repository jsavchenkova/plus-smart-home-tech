package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ScenarioConditionCompositeKey implements Serializable {
    @Column(insertable=false, updatable=false)
    private Long scenario_id;
    @Column(insertable=false, updatable=false)
    private String sensor_id;
    @Column(insertable=false, updatable=false)
    private Long condition_id;
}
