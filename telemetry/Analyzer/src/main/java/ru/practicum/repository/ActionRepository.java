package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ActionEntity;

public interface ActionRepository extends JpaRepository<ActionEntity, Long> {
}
