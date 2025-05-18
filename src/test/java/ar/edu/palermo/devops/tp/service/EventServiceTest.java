package ar.edu.palermo.devops.tp.service;

import ar.edu.palermo.devops.tp.exception.EventNotFoundException;
import ar.edu.palermo.devops.tp.model.Event;
import ar.edu.palermo.devops.tp.model.dto.EventDto;
import ar.edu.palermo.devops.tp.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventService underTest;

    @DisplayName("Should save event")
    @Test
    public void save_whenDataIsOk_ShouldSave() {
        // Given
        final Long id = 1L;
        final String name = "Iron Maiden Concert";
        final String description = "Iron Maiden Concert in Buenos Aires";
        final LocalDateTime date = LocalDateTime.of(2025, 10, 1, 20, 0);

        final EventDto dto = new EventDto(null, name, description, date);

        final Event mappedEvent = Event.builder()
                .name(name)
                .description(description)
                .date(date)
                .build();

        final Event eventSaved = Event.builder()
                .id(id)
                .name(name)
                .description(description)
                .date(date)
                .build();

        given(modelMapper.map(dto, Event.class)).willReturn(mappedEvent);
        given(eventRepository.save(mappedEvent)).willReturn(eventSaved);
        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);

        // When
        final Event result = underTest.save(dto);

        // Then
        verify(modelMapper).map(dto, Event.class);
        verify(eventRepository).save(eventArgumentCaptor.capture());

        final Event eventCaptured = eventArgumentCaptor.getValue();
        Assertions.assertThat(eventCaptured.getName()).isEqualTo(name);
        Assertions.assertThat(eventCaptured.getDescription()).isEqualTo(description);
        Assertions.assertThat(eventCaptured.getDate()).isEqualTo(date);
        Assertions.assertThat(eventCaptured.getId()).isNull();
        assertThat(result).isEqualTo(eventSaved);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @DisplayName("Should return all events")
    @Test
    public void findAll_whenRepositoryHasEvents_ShouldReturnAllEvents() {
        // Given
        final Event event1 = Event.builder().id(1L).name("Event 1").description("Description 1").build();
        final Event event2 = Event.builder().id(2L).name("Event 2").description("Description 2").build();
        given(eventRepository.findAll()).willReturn(List.of(event1, event2));

        // When
        final List<Event> result = underTest.findAll();

        // Then
        verify(eventRepository, times(1)).findAll();
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(event1, event2);
    }

    @DisplayName("Should return empty list when repository has no events")
    @Test
    public void findAll_whenRepositoryIsEmpty_ShouldReturnEmptyList() {
        // Given
        given(eventRepository.findAll()).willReturn(List.of());

        // When
        final List<Event> result = underTest.findAll();

        // Then
        verify(eventRepository, times(1)).findAll();
        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @DisplayName("Should return event when ID exists")
    @Test
    public void findById_whenIdExists_ShouldReturnEvent() {
        // Given
        final Long id = 1L;
        final String name = "Iron Maiden Concert";
        final String description = "Iron Maiden Concert in Buenos Aires";
        final LocalDateTime date = LocalDateTime.of(2025, 10, 1, 20, 0);
        final Event event = Event.builder()
                .id(id)
                .name(name)
                .description(description)
                .date(date)
                .build();
        given(eventRepository.findById(id)).willReturn(Optional.of(event));

        // When
        final Event result = underTest.findById(id);

        // Then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(eventRepository).findById(idArgumentCaptor.capture());
        final Long idCaptured = idArgumentCaptor.getValue();
        assertThat(idCaptured).isEqualTo(id);

        assertThat(result).isEqualTo(event);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @DisplayName("Should throw exception when ID does not exist")
    @Test
    public void findById_whenIdDoesNotExist_ShouldThrowException() {
        // Given
        final Long nonExistentEventId = 1L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);
        given(eventRepository.findById(nonExistentEventId)).willReturn(Optional.empty());

        // When
        // Then
        Assertions.assertThatThrownBy(() -> underTest.findById(nonExistentEventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(ERROR_MSG);
    }

    @DisplayName("Should update event when ID exists")
    @Test
    public void update_whenIdExists_ShouldUpdateEvent() {
        // Given
        final Long id = 1L;
        final String name = "Iron Maiden Concert";
        final String description = "Iron Maiden Concert in Buenos Aires";
        final LocalDateTime date = LocalDateTime.of(2025, 10, 1, 20, 0);
        final EventDto dto = new EventDto(null, name, description, date);

        final Event eventToUpdate = Event.builder()
                .id(id)
                .name("Old Name")
                .description("Old Description")
                .date(LocalDateTime.of(2024, 10, 1, 20, 0))
                .build();

        final Event updatedEvent = Event.builder()
                .id(id)
                .name(name)
                .description(description)
                .date(date)
                .build();

        given(eventRepository.findById(id)).willReturn(Optional.of(eventToUpdate));
        doAnswer(invocation -> {
            EventDto toMap = invocation.getArgument(0);
            Event event = invocation.getArgument(1);
            event.setName(toMap.name());
            event.setDescription(toMap.description());
            event.setDate(toMap.date());
            return null;
        }).when(modelMapper).map(dto, eventToUpdate);


        given(eventRepository.save(Mockito.any(Event.class))).willReturn(updatedEvent);

        // When
        final Event result = underTest.update(id, dto);

        // Then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(eventRepository).findById(idArgumentCaptor.capture());
        Assertions.assertThat(idArgumentCaptor.getValue()).isEqualTo(id);

        ArgumentCaptor<EventDto> eventDtoArgumentCaptor = ArgumentCaptor.forClass(EventDto.class);
        verify(modelMapper).map(eventDtoArgumentCaptor.capture(), Mockito.any(Event.class));
        final EventDto eventDtoCaptured = eventDtoArgumentCaptor.getValue();
        Assertions.assertThat(eventDtoCaptured).isEqualTo(dto);
        Assertions.assertThat(eventDtoCaptured.name()).isEqualTo(name);
        Assertions.assertThat(eventDtoCaptured.description()).isEqualTo(description);
        Assertions.assertThat(eventDtoCaptured.date()).isEqualTo(date);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventArgumentCaptor.capture());
        final Event eventCaptured = eventArgumentCaptor.getValue();
        Assertions.assertThat(eventCaptured.getId()).isEqualTo(id);
        Assertions.assertThat(eventCaptured.getName()).isEqualTo(name);
        Assertions.assertThat(eventCaptured.getDescription()).isEqualTo(description);
        Assertions.assertThat(eventCaptured.getDate()).isEqualTo(date);
        Assertions.assertThat(result).isEqualTo(updatedEvent);
    }

    @DisplayName("Should throw exception when ID does not exist")
    @Test
    public void update_whenIdDoesNotExist_ShouldThrowException() {
        // Given
        final Long nonExistentEventId = 1L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);
        final EventDto dto = new EventDto(null, "Name", "Description", LocalDateTime.now());

        given(eventRepository.findById(nonExistentEventId)).willReturn(Optional.empty());

        // When
        // Then
        Assertions.assertThatThrownBy(() -> underTest.update(nonExistentEventId, dto))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(ERROR_MSG);

        verify(eventRepository).findById(nonExistentEventId);
        verify(modelMapper, never()).map(any(EventDto.class), any(Event.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @DisplayName("Should delete event when ID exists")
    @Test
    public void delete_whenIdExists_ShouldDeleteEvent() {
        // Given
        final Long id = 1L;
        final Event eventToDelete = Event.builder().id(id).name("Event Name").description("Event Description").build();
        given(eventRepository.findById(id)).willReturn(Optional.of(eventToDelete));

        // When
        underTest.delete(id);

        // Then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(eventRepository, times(1)).findById(idArgumentCaptor.capture());
        Assertions.assertThat(idArgumentCaptor.getValue()).isEqualTo(id);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).delete(eventArgumentCaptor.capture());
        final Event eventCaptured = eventArgumentCaptor.getValue();
        Assertions.assertThat(eventCaptured).isEqualTo(eventToDelete);
    }

    @DisplayName("Should throw exception when ID does not exist")
    @Test
    public void delete_whenIdDoesNotExist_ShouldThrowException() {
        // Given
        final Long nonExistentEventId = 1L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentEventId);
        given(eventRepository.findById(nonExistentEventId)).willReturn(Optional.empty());

        // When  Then
        Assertions.assertThatThrownBy(() -> underTest.delete(nonExistentEventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(ERROR_MSG);

        verify(eventRepository).findById(nonExistentEventId);
        verify(eventRepository, never()).delete(any(Event.class));
    }
}
