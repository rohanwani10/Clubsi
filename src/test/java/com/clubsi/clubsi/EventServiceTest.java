package com.clubsi.clubsi;

import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Entity.User;
import com.clubsi.clubsi.Repository.EventRegistrationRepository;
import com.clubsi.clubsi.Repository.EventRepository;
import com.clubsi.clubsi.Repository.UserRepository;
import com.clubsi.clubsi.Service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventRegistrationRepository eventRegistrationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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
        testEvent.setRegistrationDeadline(System.currentTimeMillis() + 43200000); // 12 hours from now
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
    void testCreateEvent_Success() {
        // Arrange
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        ResponseEntity<String> response = eventService.createEvent(testEvent);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("event123"));
        assertTrue(response.getBody().contains("Spring Boot Workshop"));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEvent_NullEvent() {
        // Act
        ResponseEntity<String> response = eventService.createEvent(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Invalid event data", response.getBody());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testCreateEvent_EmptyEventName() {
        // Arrange
        testEvent.setEventName("");

        // Act
        ResponseEntity<String> response = eventService.createEvent(testEvent);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testGetAllEvents_Success() {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        ResponseEntity<String> response = eventService.getAllEvents();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Spring Boot Workshop"));
        assertTrue(response.getBody().contains("event123"));
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetEventById_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));

        // Act
        ResponseEntity<String> response = eventService.getEventById("event123");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("event123"));
        assertTrue(response.getBody().contains("Spring Boot Workshop"));
        verify(eventRepository, times(1)).findById("event123");
    }

    @Test
    void testGetEventById_NotFound() {
        // Arrange
        when(eventRepository.findById("invalid123")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = eventService.getEventById("invalid123");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals("Event not found", response.getBody());
    }

    @Test
    void testGetEventsByStatus_Success() {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findByStatus("UPCOMING")).thenReturn(events);

        // Act
        ResponseEntity<String> response = eventService.getEventsByStatus("UPCOMING");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Spring Boot Workshop"));
        verify(eventRepository, times(1)).findByStatus("UPCOMING");
    }

    @Test
    void testUpdateEvent_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event updatedEvent = new Event();
        updatedEvent.setEventName("Updated Workshop");
        updatedEvent.setVenue("New Venue");

        // Act
        ResponseEntity<String> response = eventService.updateEvent("event123", updatedEvent);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Event updated successfully"));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testDeleteEvent_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).deleteById("event123");

        // Act
        ResponseEntity<String> response = eventService.deleteEvent("event123");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Event deleted successfully"));
        verify(eventRepository, times(1)).deleteById("event123");
    }

    @Test
    void testRegisterForEvent_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(null);
        when(eventRegistrationRepository.save(any(EventRegistration.class))).thenReturn(testRegistration);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        EventRegistration newRegistration = new EventRegistration();
        newRegistration.setUser(testUser);

        // Act
        ResponseEntity<String> response = eventService.registerForEvent("event123", newRegistration);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Registration successful"));
        verify(eventRegistrationRepository, times(1)).save(any(EventRegistration.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testRegisterForEvent_EventFull() {
        // Arrange
        testEvent.setMaxParticipants(1);
        testEvent.setCurrentParticipants(1);
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));

        EventRegistration newRegistration = new EventRegistration();
        newRegistration.setUser(testUser);

        // Act
        ResponseEntity<String> response = eventService.registerForEvent("event123", newRegistration);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Event is full", response.getBody());
        verify(eventRegistrationRepository, never()).save(any(EventRegistration.class));
    }

    @Test
    void testRegisterForEvent_DeadlinePassed() {
        // Arrange
        testEvent.setRegistrationDeadline(System.currentTimeMillis() - 1000); // Past deadline
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));

        EventRegistration newRegistration = new EventRegistration();
        newRegistration.setUser(testUser);

        // Act
        ResponseEntity<String> response = eventService.registerForEvent("event123", newRegistration);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Registration deadline has passed", response.getBody());
    }

    @Test
    void testRegisterForEvent_AlreadyRegistered() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(testRegistration);

        EventRegistration newRegistration = new EventRegistration();
        newRegistration.setUser(testUser);

        // Act
        ResponseEntity<String> response = eventService.registerForEvent("event123", newRegistration);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("User already registered for this event", response.getBody());
    }

    @Test
    void testMarkAttendance_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(testRegistration);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventRegistrationRepository.save(any(EventRegistration.class))).thenReturn(testRegistration);

        // Act
        ResponseEntity<String> response = eventService.markAttendance("event123", "testuser");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Attendance marked successfully"));
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(eventRegistrationRepository, times(1)).save(any(EventRegistration.class));
    }

    @Test
    void testMarkAttendance_NotRegistered() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(null);

        // Act
        ResponseEntity<String> response = eventService.markAttendance("event123", "testuser");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("User is not registered for this event", response.getBody());
    }

    @Test
    void testGetAttendedUsers_Success() {
        // Arrange
        LinkedList<User> attendedUsers = new LinkedList<>();
        attendedUsers.add(testUser);
        testEvent.setAttendedUsers(attendedUsers);
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));

        // Act
        ResponseEntity<String> response = eventService.getAttendedUsers("event123");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("testuser"));
        assertTrue(response.getBody().contains("test@example.com"));
    }

    @Test
    void testGetEventRegistrations_Success() {
        // Arrange
        List<EventRegistration> registrations = Arrays.asList(testRegistration);
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(eventRegistrationRepository.findByEvent(testEvent)).thenReturn(registrations);

        // Act
        ResponseEntity<String> response = eventService.getEventRegistrations("event123");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("testuser"));
        assertTrue(response.getBody().contains("PENDING"));
        assertTrue(response.getBody().contains("REGISTERED"));
    }

    @Test
    void testCancelRegistration_Success() {
        // Arrange
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(testRegistration);
        doNothing().when(eventRegistrationRepository).delete(testRegistration);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        testEvent.setCurrentParticipants(5);

        // Act
        ResponseEntity<String> response = eventService.cancelRegistration("event123", "testuser");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("Registration cancelled successfully"));
        verify(eventRegistrationRepository, times(1)).delete(testRegistration);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelRegistration_EventAlreadyStarted() {
        // Arrange
        testEvent.setEventDate(System.currentTimeMillis() - 1000); // Past event
        when(eventRepository.findById("event123")).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByEventAndUser(testEvent, testUser)).thenReturn(testRegistration);

        // Act
        ResponseEntity<String> response = eventService.cancelRegistration("event123", "testuser");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Cannot cancel registration for past events", response.getBody());
    }

    @Test
    void testGetUserRegistrations_Success() {
        // Arrange
        List<EventRegistration> registrations = Arrays.asList(testRegistration);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(eventRegistrationRepository.findByUser(testUser)).thenReturn(registrations);

        // Act
        ResponseEntity<String> response = eventService.getUserRegistrations("testuser");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("event123"));
        assertTrue(response.getBody().contains("Spring Boot Workshop"));
    }

    @Test
    void testGetUserRegistrations_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("invalid")).thenReturn(null);

        // Act
        ResponseEntity<String> response = eventService.getUserRegistrations("invalid");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
    }
}
