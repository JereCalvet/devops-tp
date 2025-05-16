package ar.edu.palermo.devops.tp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {
    private static final String EVENT_ID_NOT_FOUND_ERROR_MSG = "Event id %d not found.";

    public EventNotFoundException(Long eventId) {
        super(String.format(EVENT_ID_NOT_FOUND_ERROR_MSG, eventId));
    }
}
