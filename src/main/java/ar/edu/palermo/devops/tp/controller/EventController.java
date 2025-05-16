package ar.edu.palermo.devops.tp.controller;

import ar.edu.palermo.devops.tp.model.dto.EventDto;
import ar.edu.palermo.devops.tp.model.Event;
import ar.edu.palermo.devops.tp.service.EventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventServiceInterface eventSvc;

    public EventController(EventServiceInterface eventSvc) {
        this.eventSvc = eventSvc;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid EventDto event) {
        Event savedEvent = eventSvc.save(event);
        return ResponseEntity.created(URI.create(String.format("/api/v1/events/%d", savedEvent.getId()))).body(savedEvent);
    }

    @GetMapping()
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventSvc.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventSvc.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody @Valid EventDto event) {
        return ResponseEntity.ok(eventSvc.update(id, event));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> patchEvent(@PathVariable Long id, @RequestBody @Valid EventDto event) {
        return ResponseEntity.ok(eventSvc.update(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventSvc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
