package ar.edu.palermo.devops.tp.controller;

import ar.edu.palermo.devops.tp.exception.EventNotFoundException;
import ar.edu.palermo.devops.tp.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ar.edu.palermo.devops.tp.model.Event;
import ar.edu.palermo.devops.tp.model.dto.EventDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_EVENTS_BASE_URL = "/api/v1/events";

    @DisplayName("createEvent returns created event and location header when input is valid")
    @Test
    void createEvent_WhenDataIsOk_ShouldReturnsCreatedEventAndLocationHeader() {
        //given
        final var eventId = 100L;
        final var name = "AC/DC Concert";
        final var description = "AC/DC concert in Luna Park";
        final var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        final var eventDto = new EventDto(null, name, description, date);

        final var savedEvent = Event.builder()
                .id(eventId)
                .name(name)
                .description(description)
                .date(date)
                .build();

        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        Mockito.when(eventService.save(eventDtoArgumentCaptor.capture())).thenReturn(savedEvent);

        //when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Location",  containsString(API_EVENTS_BASE_URL + "/" + eventId)))
                    .andExpect(jsonPath("id").value(eventId))
                    .andExpect(jsonPath("name").value(name))
                    .andExpect(jsonPath("description").value(description))
                    .andExpect(jsonPath("date").value(date.toString()))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        final EventDto capturedEventDto = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(capturedEventDto.name()).isEqualTo(name);
        Assertions.assertThat(capturedEventDto.description()).isEqualTo(description);
        Assertions.assertThat(capturedEventDto.date()).isEqualTo(date);
        Assertions.assertThat(capturedEventDto.id()).isNull();
        Assertions.assertThat(capturedEventDto).isEqualTo(eventDto);
        Mockito.verify(eventService, times(1)).save(any(EventDto.class));
    }

    @DisplayName("createEvent returns bad request when name input is shorter than 3 characters")
    @Test
    void createEvent_withInvalidName_ShouldReturnBadRequest() {
        //given
        var shortName = "AC";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, shortName, description, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when name input is longer than 50 characters")
    @Test
    void createEvent_withLongName_ShouldReturnBadRequest() {
        //given
        var longName = "AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, longName, description, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when name input is blank")
    @Test
    void createEvent_withBlankName_ShouldReturnBadRequest() {
        //given
        var blankName = "            ";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, blankName, description, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when name input is null")
    @Test
    void createEvent_withNullName_ShouldReturnBadRequest() {
        //given
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, null, description, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when description input is shorter than 10 characters")
    @Test
    void createEvent_withShortDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var shortDescription = "AC";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, shortDescription, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when description input is longer than 200 characters")
    @Test
    void createEvent_withLongDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var longDescription = "AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC ConcertAC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, longDescription, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when description input is blank")
    @Test
    void createEvent_withBlankDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var blankDescription = "            ";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, blankDescription, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when description input is null")
    @Test
    void createEvent_withNullDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, null, date);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("createEvent returns bad request when date input is invalid")
    @Test
    void createEvent_withPastDate_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var pastDate = LocalDateTime.of(2024, 11, 5, 20, 0);

        var dto = new EventDto(null, name, description, pastDate);

        // when & then
        try {
            mockMvc.perform(post(API_EVENTS_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.date").value("Date must be in the future"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).save(any());
    }

    @DisplayName("getEvents returns a list of events when events exist")
    @Test
    void getEventsReturnsListOfEvents() {
        //given
        final var event1 = Event.builder()
                .name("Iron Maiden Concert")
                .description("Iron Maiden concert in Estadio River Plate")
                .date(LocalDateTime.of(2025, 10, 1, 10, 0))
                .build();
        final var event2 = Event.builder()
                .name("Metallica Concert")
                .description("Metallica concert in Estadio River Plate")
                .date(LocalDateTime.of(2025, 10, 2, 10, 0))
                .build();
        List<Event> events = List.of(event1, event2);
        given(eventService.findAll()).willReturn(events);

        //when & then
        try {
            mockMvc.perform(get(API_EVENTS_BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(events.size()))
                    .andExpect(jsonPath("$", hasSize(events.size())))
                    .andExpect(jsonPath("$[0].id").value(event1.getId()))
                    .andExpect(jsonPath("$[0].name").value(event1.getName()))
                    .andExpect(jsonPath("$[0].description").value(event1.getDescription()))
                    .andExpect(jsonPath("$[0].date").value(event1.getDate().toString()))
                    .andExpect(jsonPath("$[1].id").value(event2.getId()))
                    .andExpect(jsonPath("$[1].name").value(event2.getName()))
                    .andExpect(jsonPath("$[1].description").value(event2.getDescription()))
                    .andExpect(jsonPath("$[1].date").value(event2.getDate().toString()))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        Mockito.verify(eventService, times(1)).findAll();
    }

    @DisplayName("getEvents returns an empty list when no events exist")
    @Test
    void getEventsReturnsEmptyListWhenNoEventsExist() {
        //given
        List<Event> events = List.of();
        given(eventService.findAll()).willReturn(events);

        //when & then
        try {
            mockMvc.perform(get(API_EVENTS_BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, times(1)).findAll();
    }

    @Test
    @DisplayName("getEventById returns event when event exists")
    void getEventById_WhenEventExists_ShouldReturnEvent() {
        // given
        final var eventId = 1L;
        final var name = "AC/DC Concert";
        final var description = "AC/DC concert in Luna Park";
        final var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        final var event = Event.builder()
                .id(eventId)
                .name(name)
                .description(description)
                .date(date)
                .build();

        given(eventService.findById(eventId)).willReturn(event);

        // when & then
        try {
            mockMvc.perform(get(API_EVENTS_BASE_URL + "/{id}", eventId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(eventId))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.date").value(date.toString()))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        Mockito.verify(eventService, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("getEventById returns not found when event does not exist")
    void getEventById_WhenEventDoesNotExist_ShouldReturnNotFound() {
        // given
        final var nonExistentEventId = 999L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);
        given(eventService.findById(nonExistentEventId)).willThrow(new EventNotFoundException(nonExistentEventId));

        // when & then
        try {
            mockMvc.perform(get(API_EVENTS_BASE_URL+ "/{id}", nonExistentEventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                            .isInstanceOf(EventNotFoundException.class))
                    .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                            .isEqualTo(ERROR_MSG))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        Mockito.verify(eventService, times(1)).findById(nonExistentEventId);
    }

    @DisplayName("updateEvent updates and returns the event when input is valid")
    @Test
    void updateEvent_WhenDataIsOk_ShouldReturnUpdatedEvent() {
        // given
        final var eventId = 1L;
        final var name = "Updated Concert";
        final var description = "Updated concert description";
        final var date = LocalDateTime.of(2025, 12, 10, 18, 0);

        final var eventDto = new EventDto(null, name, description, date);

        final var updatedEvent = Event.builder()
                .id(eventId)
                .name(name)
                .description(description)
                .date(date)
                .build();

        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        given(eventService.update(eventIdArgumentCaptor.capture(), eventDtoArgumentCaptor.capture()))
                .willReturn(updatedEvent);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", eventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(eventId))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.date").value(date.toString()))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        final Long idCapturedRequestValue = eventIdArgumentCaptor.getValue();
        Assertions.assertThat(idCapturedRequestValue).isEqualTo(eventId);

        final EventDto eventDtoCapturedRequestValue = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(eventDtoCapturedRequestValue.name()).isEqualTo(name);
        Assertions.assertThat(eventDtoCapturedRequestValue.description()).isEqualTo(description);
        Assertions.assertThat(eventDtoCapturedRequestValue.date()).isEqualTo(date);
        Assertions.assertThat(eventDtoCapturedRequestValue.id()).isNull();

        Mockito.verify(eventService, times(1)).update(eventId, eventDto);
    }

    @DisplayName("updateEvent returns not found when event does not exist")
    @Test
    void updateEvent_WhenEventDoesNotExist_ShouldReturnNotFound() {
        // given
        final var nonExistentEventId = 999L;
        final var name = "Non-existent Concert";
        final var description = "Non-existent concert description";
        final var date = LocalDateTime.of(2025, 12, 10, 18, 0);

        final var eventDto = new EventDto(null, name, description, date);

        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);

        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        given(eventService.update(eventIdArgumentCaptor.capture(), eventDtoArgumentCaptor.capture()))
                .willThrow(new EventNotFoundException(nonExistentEventId));

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", nonExistentEventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                            .isInstanceOf(EventNotFoundException.class))
                    .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                            .isEqualTo(ERROR_MSG))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        final Long idCapturedRequestValue = eventIdArgumentCaptor.getValue();
        Assertions.assertThat(idCapturedRequestValue).isEqualTo(nonExistentEventId);
        final EventDto eventDtoCapturedRequestValue = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(eventDtoCapturedRequestValue.name()).isEqualTo(name);
        Assertions.assertThat(eventDtoCapturedRequestValue.description()).isEqualTo(description);
        Assertions.assertThat(eventDtoCapturedRequestValue.date()).isEqualTo(date);
        Assertions.assertThat(eventDtoCapturedRequestValue.id()).isNull();

        Mockito.verify(eventService, times(1)).update(nonExistentEventId, eventDto);
    }

    @DisplayName("updateEvent returns bad request when name input is shorter than 3 characters")
    @Test
    void updateEvent_withInvalidName_ShouldReturnBadRequest() {
        //given
        var shortName = "AC";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, shortName, description, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when name input is longer than 50 characters")
    @Test
    void updateEvent_withLongName_ShouldReturnBadRequest() {
        //given
        var longName = "AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, longName, description, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when name input is blank")
    @Test
    void updateEvent_withBlankName_ShouldReturnBadRequest() {
        //given
        var blankName = "            ";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, blankName, description, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when name input is null")
    @Test
    void updateEvent_withNullName_ShouldReturnBadRequest() {
        //given
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, null, description, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when description input is null")
    @Test
    void updateEvent_withNullDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, null, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when description input is blank")
    @Test
    void updateEvent_withBlankDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var blankDescription = "            ";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, blankDescription, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when description input is shorter than 10 characters")
    @Test
    void updateEvent_withShortDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var shortDescription = "AC";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, shortDescription, date);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when description input is longer than 200 characters")
    @Test
    void updateEvent_withLongDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var longDescription = "AC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertAC/DC ConcertConcertAC/DC ConcertAC/DC ConcertConcertAC/DC ConcertAC/DC ConcertConcertAC/DC ConcertAC/DC ConcertConcertAC/DC ConcertAC/DC Concert";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);
        var dto = new EventDto(null, name, longDescription, date);
        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("updateEvent returns bad request when date input is in the past")
    @Test
    void updateEvent_withPastDate_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var pastDate = LocalDateTime.of(2024, 11, 5, 20, 0);

        var dto = new EventDto(null, name, description, pastDate);

        // when & then
        try {
            mockMvc.perform(put(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.date").value("Date must be in the future"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent updates and returns the event when input is valid")
    @Test
    void patchEvent_WhenDataIsOk_ShouldReturnUpdatedEvent() {
        // given
        final var eventId = 1L;
        final var name = "Updated Concert";
        final var description = "Updated concert description";
        final var date = LocalDateTime.of(2025, 12, 10, 18, 0);

        final var eventDto = new EventDto(null, name, description, date);

        final var updatedEvent = Event.builder()
                .id(eventId)
                .name(name)
                .description(description)
                .date(date)
                .build();

        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        given(eventService.update(eventIdArgumentCaptor.capture(), eventDtoArgumentCaptor.capture()))
                .willReturn(updatedEvent);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", eventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(eventId))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.date").value(date.toString()))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        final Long idCapturedRequestValue = eventIdArgumentCaptor.getValue();
        Assertions.assertThat(idCapturedRequestValue).isEqualTo(eventId);

        final EventDto eventDtoCapturedRequestValue = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(eventDtoCapturedRequestValue.name()).isEqualTo(name);
        Assertions.assertThat(eventDtoCapturedRequestValue.description()).isEqualTo(description);
        Assertions.assertThat(eventDtoCapturedRequestValue.date()).isEqualTo(date);
        Assertions.assertThat(eventDtoCapturedRequestValue.id()).isNull();

        Mockito.verify(eventService, times(1)).update(eventId, eventDto);
    }

    @DisplayName("patchEvent returns not found when event does not exist")
    @Test
    void patchEvent_WhenEventDoesNotExist_ShouldReturnNotFound() {
        // given
        final var nonExistentEventId = 999L;
        final var name = "Non-existent Concert";
        final var description = "Non-existent concert description";
        final var date = LocalDateTime.of(2025, 12, 10, 18, 0);

        final var eventDto = new EventDto(null, name, description, date);

        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);

        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        given(eventService.update(eventIdArgumentCaptor.capture(), eventDtoArgumentCaptor.capture()))
                .willThrow(new EventNotFoundException(nonExistentEventId));

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", nonExistentEventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                            .isInstanceOf(EventNotFoundException.class))
                    .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                            .isEqualTo(ERROR_MSG))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        final Long idCapturedRequestValue = eventIdArgumentCaptor.getValue();
        Assertions.assertThat(idCapturedRequestValue).isEqualTo(nonExistentEventId);
        final EventDto eventDtoCapturedRequestValue = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(eventDtoCapturedRequestValue.name()).isEqualTo(name);
        Assertions.assertThat(eventDtoCapturedRequestValue.description()).isEqualTo(description);
        Assertions.assertThat(eventDtoCapturedRequestValue.date()).isEqualTo(date);
        Assertions.assertThat(eventDtoCapturedRequestValue.id()).isNull();

        Mockito.verify(eventService, times(1)).update(nonExistentEventId, eventDto);
    }

    @DisplayName("patchEvent returns bad request when name input is shorter than 3 characters")
    @Test
    void patchEvent_withInvalidName_ShouldReturnBadRequest() {
        //given
        var shortName = "AC";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, shortName, description, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when name input is longer than 50 characters")
    @Test
    void patchEvent_withLongName_ShouldReturnBadRequest() {
        //given
        var longName = "AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, longName, description, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name must be between 3 and 50 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when name input is blank")
    @Test
    void patchEvent_withBlankName_ShouldReturnBadRequest() {
        //given
        var blankName = "            ";
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, blankName, description, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when name input is null")
    @Test
    void patchEvent_withNullName_ShouldReturnBadRequest() {
        //given
        var description = "AC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, null, description, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Name cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when description input is shorter than 10 characters")
    @Test
    void patchEvent_withShortDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var shortDescription = "AC";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, shortDescription, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when description input is longer than 200 characters")
    @Test
    void patchEvent_withLongDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var longDescription = "AC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna ParkAC/DC concert in Luna Park";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);
        var dto = new EventDto(null, name, longDescription, date);
        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description must be between 10 and 200 characters"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when description input is blank")
    @Test
    void patchEvent_withBlankDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var blankDescription = "            ";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, blankDescription, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when description input is null")
    @Test
    void patchEvent_withNullDescription_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var date = LocalDateTime.of(2025, 11, 5, 20, 0);

        var dto = new EventDto(null, name, null, date);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("patchEvent returns bad request when date input is in the past")
    @Test
    void patchEvent_withPastDate_ShouldReturnBadRequest() {
        //given
        var name = "AC/DC Concert";
        var description = "AC/DC concert in Luna Park";
        var pastDate = LocalDateTime.of(2024, 11, 5, 20, 0);

        var dto = new EventDto(null, name, description, pastDate);

        // when & then
        try {
            mockMvc.perform(patch(API_EVENTS_BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.date").value("Date must be in the future"))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }
        Mockito.verify(eventService, never()).update(any(), any());
    }

    @DisplayName("deleteEvent deletes the event and returns no content")
    @Test
    void deleteEvent_WhenEventExists_ShouldReturnNoContent() {
        // given
        final var eventId = 1L;
        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.doNothing().when(eventService).delete(eventIdArgumentCaptor.capture());

        // when & then
        try {
            mockMvc.perform(delete(API_EVENTS_BASE_URL + "/{id}", eventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        Mockito.verify(eventService, times(1)).delete(eventId);
        Assertions.assertThat(eventIdArgumentCaptor.getValue()).isEqualTo(eventId);
    }

    @DisplayName("deleteEvent returns not found when event does not exist")
    @Test
    void deleteEvent_WhenEventDoesNotExist_ShouldReturnNotFound() {
        // given
        final var nonExistentEventId = 999L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);

        ArgumentCaptor<Long> eventIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.doThrow(new EventNotFoundException(nonExistentEventId)).when(eventService).delete(eventIdArgumentCaptor.capture());

        // when & then
        try {
            mockMvc.perform(delete(API_EVENTS_BASE_URL + "/{id}", nonExistentEventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                            .isInstanceOf(EventNotFoundException.class))
                    .andExpect(result -> Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                            .isEqualTo(ERROR_MSG))
                    .andDo(print());
        } catch (Exception e) {
            Assertions.fail("Should not throw any exception");
        }

        Mockito.verify(eventService, times(1)).delete(nonExistentEventId);
        Assertions.assertThat(eventIdArgumentCaptor.getValue()).isEqualTo(nonExistentEventId);
    }
}
