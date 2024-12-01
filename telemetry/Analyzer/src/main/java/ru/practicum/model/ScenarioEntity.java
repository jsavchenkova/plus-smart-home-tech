package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "scenarios")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String hubId;
    private String name;
}
