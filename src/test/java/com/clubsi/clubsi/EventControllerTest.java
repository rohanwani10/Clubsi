package com.clubsi.clubsi;

import com.clubsi.clubsi.Controller.EventController;
import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Entity.User;
import com.clubsi.clubsi.Service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private Event testEvent;
    private User testUser;
    private EventRegistration testRegistration;

    @BeforeEach
    void setUp() {
        // Create test event
        testEvent = new Event();
        testEvent.setId("event123");
        testEvent.setEventName("Spring Boot Workshop");
        testEvent.setDescription("Advanced Spring Boot training");
        testEvent.setEventDate(System.currentTimeMillis() + 86400000); // Tomorrow
        testEvent.setVenue("Seminar Hall A");
        testEvent.setRegistrationDeadline(System.currentTimeMillis() + 43200000);
        testEvent.setMaxParticipants(50);
        testEvent.setCurrentParticipants(0);
        testEvent.setEventType("TECHNICAL");
        testEvent.setFee("500");
        testEvent.setStatus("UPCOMING");
        testEvent.setCreatedAt(System.currentTimeMillis());
        testEvent.setUpdatedAt(System.currentTimeMillis());
        testEvent.setAttendedUsers(new LinkedList<>());

        // Create test user
        testUser = new User();
        testUser.setId("user123");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhone("9876543210");

        // Create test registration
        testRegistration = new EventRegistration();
        testRegistration.setId("reg123");
        testRegistration.setEvent(testEvent);
        testRegistration.setUser(testUser);
        testRegistration.setRegistrationDate(System.currentTimeMillis());
        testRegistration.setPaymentStatus("PENDING");
        testRegistration.setAttendanceStatus("REGISTERED");
    }

    @Test
    void testCreateEvent_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Event created successfully\",\"eventId\":\"event123\"}";
        when(eventService.createEvent(any(Event.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(successResponse));

        // Act & Assert
        mockMvc.perform(post("/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isCreated())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    void testCreateEvent_InvalidData() throws Exception {
        // Arrange
        when(eventService.createEvent(any(Event.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid event data"));

        // Act & Assert
        mockMvc.perform(post("/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid event data"));
    }

    @Test
    void testGetAllEvents_Success() throws Exception {
        // Arrange
        String eventsJson = "[{\"id\":\"event123\",\"eventName\":\"Spring Boot Workshop\"}]";
        when(eventService.getAllEvents())
                .thenReturn(ResponseEntity.ok(eventsJson));

        // Act & Assert
        mockMvc.perform(get("/events/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(eventsJson));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetEventById_Success() throws Exception {
        // Arrange
        String eventJson = "{\"id\":\"event123\",\"eventName\":\"Spring Boot Workshop\"}";
        when(eventService.getEventById("event123"))
                .thenReturn(ResponseEntity.ok(eventJson));

        // Act & Assert
        mockMvc.perform(get("/events/event123"))
                .andExpect(status().isOk())
                .andExpect(content().json(eventJson));

        verify(eventService, times(1)).getEventById("event123");
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        // Arrange
        when(eventService.getEventById("invalid"))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found"));

        // Act & Assert
        mockMvc.perform(get("/events/invalid"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found"));
    }

    @Test
    void testGetEventsByStatus_Success() throws Exception {
        // Arrange
        String eventsJson = "[{\"id\":\"event123\",\"status\":\"UPCOMING\"}]";
        when(eventService.getEventsByStatus("UPCOMING"))
                .thenReturn(ResponseEntity.ok(eventsJson));

        // Act & Assert
        mockMvc.perform(get("/events/status/UPCOMING"))
                .andExpect(status().isOk())
                .andExpect(content().json(eventsJson));

        verify(eventService, times(1)).getEventsByStatus("UPCOMING");
    }

    @Test
    void testUpdateEvent_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Event updated successfully\"}";
        when(eventService.updateEvent(eq("event123"), any(Event.class)))
                .thenReturn(ResponseEntity.ok(successResponse));

        Event updatedEvent = new Event();
        updatedEvent.setEventName("Updated Workshop");

        // Act & Assert
        mockMvc.perform(put("/events/update/event123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).updateEvent(eq("event123"), any(Event.class));
    }

    @Test
    void testDeleteEvent_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Event deleted successfully\"}";
        when(eventService.deleteEvent("event123"))
                .thenReturn(ResponseEntity.ok(successResponse));

        // Act & Assert
        mockMvc.perform(delete("/events/delete/event123"))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).deleteEvent("event123");
    }

    @Test
    void testRegisterForEvent_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Registration successful\",\"registrationId\":\"reg123\"}";
        when(eventService.registerForEvent(eq("event123"), any(EventRegistration.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(successResponse));

        // Act & Assert
        mockMvc.perform(post("/events/register/event123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegistration)))
                .andExpect(status().isCreated())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).registerForEvent(eq("event123"), any(EventRegistration.class));
    }

    @Test
    void testRegisterForEvent_EventFull() throws Exception {
        // Arrange
        when(eventService.registerForEvent(eq("event123"), any(EventRegistration.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event is full"));

        // Act & Assert
        mockMvc.perform(post("/events/register/event123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegistration)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Event is full"));
    }

    @Test
    void testGetUserRegistrations_Success() throws Exception {
        // Arrange
        String registrationsJson = "[{\"eventId\":\"event123\",\"eventName\":\"Workshop\"}]";
        when(eventService.getUserRegistrations("testuser"))
                .thenReturn(ResponseEntity.ok(registrationsJson));

        // Act & Assert
        mockMvc.perform(get("/events/user/testuser/registrations"))
                .andExpect(status().isOk())
                .andExpect(content().json(registrationsJson));

        verify(eventService, times(1)).getUserRegistrations("testuser");
    }

    @Test
    void testMarkAttendance_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Attendance marked successfully\"}";
        when(eventService.markAttendance("event123", "testuser"))
                .thenReturn(ResponseEntity.ok(successResponse));

        // Act & Assert
        mockMvc.perform(post("/events/event123/attendance/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).markAttendance("event123", "testuser");
    }

    @Test
    void testMarkAttendance_NotRegistered() throws Exception {
        // Arrange
        when(eventService.markAttendance("event123", "testuser"))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User is not registered for this event"));

        // Act & Assert
        mockMvc.perform(post("/events/event123/attendance/testuser"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is not registered for this event"));
    }

    @Test
    void testGetAttendedUsers_Success() throws Exception {
        // Arrange
        String attendedUsersJson = "[{\"username\":\"testuser\",\"email\":\"test@example.com\"}]";
        when(eventService.getAttendedUsers("event123"))
                .thenReturn(ResponseEntity.ok(attendedUsersJson));

        // Act & Assert
        mockMvc.perform(get("/events/event123/attended"))
                .andExpect(status().isOk())
                .andExpect(content().json(attendedUsersJson));

        verify(eventService, times(1)).getAttendedUsers("event123");
    }

    @Test
    void testGetEventRegistrations_Success() throws Exception {
        // Arrange
        String registrationsJson = "[{\"userId\":\"user123\",\"paymentStatus\":\"PENDING\"}]";
        when(eventService.getEventRegistrations("event123"))
                .thenReturn(ResponseEntity.ok(registrationsJson));

        // Act & Assert
        mockMvc.perform(get("/events/event123/registrations"))
                .andExpect(status().isOk())
                .andExpect(content().json(registrationsJson));

        verify(eventService, times(1)).getEventRegistrations("event123");
    }

    @Test
    void testCancelRegistration_Success() throws Exception {
        // Arrange
        String successResponse = "{\"message\":\"Registration cancelled successfully\"}";
        when(eventService.cancelRegistration("event123", "testuser"))
                .thenReturn(ResponseEntity.ok(successResponse));

        // Act & Assert
        mockMvc.perform(delete("/events/event123/cancel/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));

        verify(eventService, times(1)).cancelRegistration("event123", "testuser");
    }

    @Test
    void testCancelRegistration_EventStarted() throws Exception {
        // Arrange
        when(eventService.cancelRegistration("event123", "testuser"))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot cancel registration for past events"));

        // Act & Assert
        mockMvc.perform(delete("/events/event123/cancel/testuser"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot cancel registration for past events"));
    }
}
