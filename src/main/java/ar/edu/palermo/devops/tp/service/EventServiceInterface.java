package ar.edu.palermo.devops.tp.service;


import ar.edu.palermo.devops.tp.model.dto.EventDto;
import ar.edu.palermo.devops.tp.model.Event;
import jakarta.validation.Valid;

import java.util.List;

public interface EventServiceInterface {
    Event findById(Long id);

    List<Event> findAll();

    Event save(EventDto event);

    Event update(Long id, @Valid EventDto event);

    void delete(Long id);
}
