package ar.edu.palermo.devops.tp.repository;

import ar.edu.palermo.devops.tp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
