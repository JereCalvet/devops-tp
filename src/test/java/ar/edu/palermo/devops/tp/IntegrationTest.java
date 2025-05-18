package ar.edu.palermo.devops.tp;

import ar.edu.palermo.devops.tp.model.Event;
import ar.edu.palermo.devops.tp.model.dto.EventDto;
import ar.edu.palermo.devops.tp.repository.EventRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest extends AbstractContainer {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private EventRepository eventRepository;

    private static final String API_EVENTS_BASE_URL = "/api/v1/events";

    @BeforeEach
    public void setup() {
        RestAssured.port = randomServerPort;
        RestAssured.basePath = API_EVENTS_BASE_URL;

        this.generateTestData();
    }

    private void generateTestData() {
        eventRepository.deleteAll();
        Event fakeEvent = Event.builder()
                .name("Iron Maiden Concert")
                .description("Iron Maiden concert in Buenos Aires")
                .date(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0))
                .build();
        Event fakeEvent2 = Event.builder()
                .name("Metallica Concert")
                .description("Metallica concert in Buenos Aires")
                .date(LocalDateTime.now().plusDays(2).withSecond(0).withNano(0))
                .build();
        eventRepository.save(fakeEvent);
        eventRepository.save(fakeEvent2);
        eventRepository.flush();
    }

    @DisplayName("Should return 201 Created when valid EventDto is provided")
    @Test
    public void shouldReturn201Created_WhenValidEventDto_IsProvided() {
        // Given
        final String name = "Test Event";
        final String description = "This is a test event";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", containsString(API_EVENTS_BASE_URL))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", is(name))
                .body("description", is(description))
                .body("date", is(date.toString()));
    }
    @Test
    @DisplayName("Should return 400 Bad Request when name is null")
    void shouldReturn400_whenNameIsNull() {
        // Given
        final String description = "This is a test event";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, null, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("name", is("Name cannot be blank"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name is too short")
    void shouldReturn400_whenNameIsTooShort() {
        // Given
        final String name = "Th";
        final String description = "This is a test event";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("name", is("Name must be between 3 and 50 characters"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when description is null")
    void shouldReturn400_whenDescriptionIsNull() {
        // Given
        final String name = "Test Event";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, null, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("description", is("Description cannot be blank"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when description is too short")
    void shouldReturn400_whenDescriptionIsTooShort() {
        // Given
        final String name = "Test Event";
        final String description = "Short";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("description", is("Description must be between 10 and 200 characters"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when description is too long")
    void shouldReturn400_whenDescriptionIsTooLong() {
        // Given
        final String name = "Test Event";
        final String description = "This is a test event with a very long description that exceeds the maximum length of 200 characters. This is just to test the validation and ensure that the system behaves as expected when the description is too long.";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("description", is("Description must be between 10 and 200 characters"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when date is in the past")
    void shouldReturn400_whenDateIsInPast() {
        // Given
        final String name = "Test Event";
        final String description = "This is a test event";
        final LocalDateTime date = LocalDateTime.now().minusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(null, name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("date", is("Date must be in the future"));
    }

    @Test
    @DisplayName("When requesting all events, should return 200 OK and a list of events")
    void shouldReturn200_whenGetAllEvents() {
        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(1));

    }

    @Test
    @DisplayName("When requesting an event by ID, should return 200 OK and the event")
    void shouldReturn200_whenGetEventById() {
        // Given
        Event event = eventRepository.findAll().get(0);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/{id}", event.getId())
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", is(event.getId().intValue()))
                .body("name", is(event.getName()))
                .body("description", is(event.getDescription()))
                .body("date", is(event.getDate().toString()));
    }

    @Test
    @DisplayName("When requesting an event by ID that does not exist, should return 404 Not Found")
    void shouldReturn404_whenGetEventByIdThatDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentId);
        final String requestPath = String.format("%s/%d",API_EVENTS_BASE_URL, nonExistentId);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/{id}", nonExistentId)
                .then()
                .log().all()
                .assertThat()
                .body("timestamp", is(LocalDateTime.now().withSecond(0).withNano(0).toString()))
                .body("status", is(HttpStatus.NOT_FOUND.value()))
                .body("error", is(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .body("message", is(ERROR_MSG))
                .body("path", is(requestPath))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("When updating an event, should return 200 OK and the updated event")
    void shouldReturn200_whenUpdateEvent() {
        // Given
        Event event = eventRepository.findAll().get(0);
        final String name = "Updated Event";
        final String description = "This is an updated test event";
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(event.getId(), name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .put("/{id}", event.getId())
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", is(event.getId().intValue()))
                .body("name", is(name))
                .body("description", is(description))
                .body("date", is(date.toString()));
    }

    @Test
    @DisplayName("When updating an event that does not exist, should return 404 Not Found")
    void shouldReturn404_whenUpdateEventThatDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentId);
        final String requestPath = String.format("%s/%d",API_EVENTS_BASE_URL, nonExistentId);

        EventDto eventDto = new EventDto(nonExistentId, "Updated Event", "This is an updated test event", LocalDateTime.now().plusDays(1).withSecond(0).withNano(0));

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .put("/{id}", nonExistentId)
                .then()
                .log().all()
                .assertThat()
                .body("timestamp", is(LocalDateTime.now().withSecond(0).withNano(0).toString()))
                .body("status", is(HttpStatus.NOT_FOUND.value()))
                .body("error", is(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .body("message", is(ERROR_MSG))
                .body("path", is(requestPath))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("When patching an event, should return 200 OK and the updated event")
    void shouldReturn200_whenPatchEvent() {
        // Given
        Event event = eventRepository.findAll().get(0);
        final String name = "Patched Event";
        final String description = event.getDescription();
        final LocalDateTime date = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        EventDto eventDto = new EventDto(event.getId(), name, description, date);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(eventDto)
                .when()
                .patch("/{id}", event.getId())
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", is(event.getId().intValue()))
                .body("name", is(name))
                .body("description", is(description))
                .body("date", is(date.toString()));
    }

    @Test
    @DisplayName("when deleting an event, should return 204 No Content")
    void shouldReturn204_whenDeleteEvent() {
        // Given
        Event event = eventRepository.findAll().get(0);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/{id}", event.getId())
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("when deleting an event that does not exist, should return 404 Not Found")
    void shouldReturn404_whenDeleteEventThatDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        final String ERROR_MSG = String.format("Event id %d not found.", nonExistentId);
        final String requestPath = String.format("%s/%d",API_EVENTS_BASE_URL, nonExistentId);

        // When Then
        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/{id}", nonExistentId)
                .then()
                .log().all()
                .assertThat()
                .body("timestamp", is(LocalDateTime.now().withSecond(0).withNano(0).toString()))
                .body("status", is(HttpStatus.NOT_FOUND.value()))
                .body("error", is(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .body("message", is(ERROR_MSG))
                .body("path", is(requestPath))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}