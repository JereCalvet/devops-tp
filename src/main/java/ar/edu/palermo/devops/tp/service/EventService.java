package ar.edu.palermo.devops.tp.service;

import ar.edu.palermo.devops.tp.model.dto.EventDto;
import ar.edu.palermo.devops.tp.exception.EventNotFoundException;
import ar.edu.palermo.devops.tp.model.Event;
import ar.edu.palermo.devops.tp.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;

@AllArgsConstructor
@Service
public class EventService implements EventServiceInterface {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    public Event save(EventDto eventToSave) {
        Event event = modelMapper.map(eventToSave, Event.class);
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public Event update(Long id, EventDto event) {
        Event eventToUpdate = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        modelMapper.map(event, eventToUpdate);
        return eventRepository.save(eventToUpdate);
    }

    public void delete(Long id) {
        Event eventToDelete = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        eventRepository.delete(eventToDelete);
    }
}
