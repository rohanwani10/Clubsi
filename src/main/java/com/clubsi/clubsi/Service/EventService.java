package com.clubsi.clubsi.Service;

import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Entity.User;
import com.clubsi.clubsi.Repository.EventRegistrationRepository;
import com.clubsi.clubsi.Repository.EventRepository;
import com.clubsi.clubsi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new event
    public ResponseEntity<String> createEvent(Event event) {
        if (event == null || event.getEventName() == null || event.getEventName().isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event data");
        }

        // Set timestamps
        long now = System.currentTimeMillis();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);

        // Initialize current participants
        if (event.getCurrentParticipants() == 0) {
            event.setCurrentParticipants(0);
        }

        // Set default status if not provided
        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus("UPCOMING");
        }

        Event saved = eventRepository.save(event);
        String resp = "{\"id\":\"" + saved.getId() + "\",\"eventName\":\"" + saved.getEventName() + "\"}";
        return ResponseEntity.status(201).body(resp);
    }

    // Get all events
    public ResponseEntity<String> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            json.append(buildEventJson(e));
            if (i < events.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return ResponseEntity.ok(json.toString());
    }

    // Get event by ID
    public ResponseEntity<String> getEventById(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }

        return ResponseEntity.ok(buildEventJson(event.get()));
    }

    // Get events by status
    public ResponseEntity<String> getEventsByStatus(String status) {
        List<Event> events = eventRepository.findByStatus(status);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            json.append(buildEventJson(events.get(i)));
            if (i < events.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return ResponseEntity.ok(json.toString());
    }

    // Update event
    public ResponseEntity<String> updateEvent(String eventId, Event updatedEvent) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        Optional<Event> existingEvent = eventRepository.findById(eventId);
        if (existingEvent.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }

        Event event = existingEvent.get();

        // Update fields if provided
        if (updatedEvent.getEventName() != null)
            event.setEventName(updatedEvent.getEventName());
        if (updatedEvent.getDescription() != null)
            event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getEventDate() > 0)
            event.setEventDate(updatedEvent.getEventDate());
        if (updatedEvent.getVenue() != null)
            event.setVenue(updatedEvent.getVenue());
        if (updatedEvent.getRegistrationDeadline() > 0)
            event.setRegistrationDeadline(updatedEvent.getRegistrationDeadline());
        if (updatedEvent.getMaxParticipants() > 0)
            event.setMaxParticipants(updatedEvent.getMaxParticipants());
        if (updatedEvent.getEventType() != null)
            event.setEventType(updatedEvent.getEventType());
        if (updatedEvent.getFee() != null)
            event.setFee(updatedEvent.getFee());
        if (updatedEvent.getStatus() != null)
            event.setStatus(updatedEvent.getStatus());
        if (updatedEvent.getPosterUrl() != null)
            event.setPosterUrl(updatedEvent.getPosterUrl());
        if (updatedEvent.getRequirements() != null)
            event.setRequirements(updatedEvent.getRequirements());
        if (updatedEvent.getContactInfo() != null)
            event.setContactInfo(updatedEvent.getContactInfo());

        event.setUpdatedAt(System.currentTimeMillis());

        Event saved = eventRepository.save(event);
        return ResponseEntity.ok("{\"message\":\"Event updated successfully\",\"id\":\"" + saved.getId() + "\"}");
    }

    // Delete event
    public ResponseEntity<String> deleteEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }

        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("{\"message\":\"Event deleted successfully\"}");
    }

    // Register user for an event
    public ResponseEntity<String> registerForEvent(String eventId, EventRegistration registration) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        // Get event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }
        Event event = eventOpt.get();

        // Check if registration deadline has passed
        if (System.currentTimeMillis() > event.getRegistrationDeadline()) {
            return ResponseEntity.status(400).body("Registration deadline has passed");
        }

        // Check if event is full
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            return ResponseEntity.status(400).body("Event is full");
        }

        // Validate and get user
        if (registration.getUser() == null) {
            return ResponseEntity.status(400).body("User information is required");
        }

        User user = null;
        if (registration.getUser().getUsername() != null) {
            user = userRepository.findByUsername(registration.getUser().getUsername());
        } else if (registration.getUser().getId() != null) {
            user = userRepository.findById(registration.getUser().getId()).orElse(null);
        }

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Check if user is already registered
        EventRegistration existing = eventRegistrationRepository.findByEventAndUser(event, user);
        if (existing != null) {
            return ResponseEntity.status(400).body("User already registered for this event");
        }

        // Create registration
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegistrationDate(System.currentTimeMillis());

        if (registration.getPaymentStatus() == null || registration.getPaymentStatus().isEmpty()) {
            registration.setPaymentStatus("PENDING");
        }
        if (registration.getAttendanceStatus() == null || registration.getAttendanceStatus().isEmpty()) {
            registration.setAttendanceStatus("REGISTERED");
        }

        EventRegistration saved = eventRegistrationRepository.save(registration);

        // Update event participant count
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        event.setUpdatedAt(System.currentTimeMillis());
        eventRepository.save(event);

        String resp = "{\"id\":\"" + saved.getId() + "\",\"message\":\"Registration successful\"}";
        return ResponseEntity.status(201).body(resp);
    }

    // Get user's registered events
    public ResponseEntity<String> getUserRegistrations(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        List<EventRegistration> registrations = eventRegistrationRepository.findByUser(user);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registrations.size(); i++) {
            EventRegistration reg = registrations.get(i);
            json.append("{");
            json.append("\"id\":\"").append(reg.getId()).append("\",");
            json.append("\"eventName\":\"").append(reg.getEvent() != null ? reg.getEvent().getEventName() : "")
                    .append("\",");
            json.append("\"eventId\":\"").append(reg.getEvent() != null ? reg.getEvent().getId() : "").append("\",");
            json.append("\"registrationDate\":").append(reg.getRegistrationDate()).append(",");
            json.append("\"paymentStatus\":\"").append(reg.getPaymentStatus()).append("\",");
            json.append("\"attendanceStatus\":\"").append(reg.getAttendanceStatus()).append("\"");
            json.append("}");
            if (i < registrations.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return ResponseEntity.ok(json.toString());
    }

    // Mark user attendance for an event
    public ResponseEntity<String> markAttendance(String eventId, String username) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid username");
        }

        // Get event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }
        Event event = eventOpt.get();

        // Get user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Check if user is registered for this event
        EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, user);
        if (registration == null) {
            return ResponseEntity.status(400).body("User is not registered for this event");
        }

        // Check if already attended
        if (event.getAttendedUsers() != null && event.getAttendedUsers().contains(user)) {
            return ResponseEntity.status(400).body("Attendance already marked");
        }

        // Initialize attendedUsers list if null
        if (event.getAttendedUsers() == null) {
            event.setAttendedUsers(new java.util.LinkedList<>());
        }

        // Mark attendance
        event.getAttendedUsers().add(user);
        registration.setAttendanceStatus("ATTENDED");
        event.setUpdatedAt(System.currentTimeMillis());

        eventRepository.save(event);
        eventRegistrationRepository.save(registration);

        return ResponseEntity.ok("{\"message\":\"Attendance marked successfully\"}");
    }

    // Get list of attended users for an event
    public ResponseEntity<String> getAttendedUsers(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }

        Event event = eventOpt.get();
        List<User> attendedUsers = event.getAttendedUsers();

        if (attendedUsers == null || attendedUsers.isEmpty()) {
            return ResponseEntity.ok("[]");
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < attendedUsers.size(); i++) {
            User u = attendedUsers.get(i);
            json.append("{");
            json.append("\"id\":\"").append(u.getId()).append("\",");
            json.append("\"username\":\"").append(u.getUsername()).append("\",");
            json.append("\"email\":\"").append(u.getEmail() != null ? u.getEmail() : "").append("\",");
            json.append("\"fullName\":\"").append(u.getFullName() != null ? u.getFullName() : "").append("\"");
            json.append("}");
            if (i < attendedUsers.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return ResponseEntity.ok(json.toString());
    }

    // Get all registrations for a specific event
    public ResponseEntity<String> getEventRegistrations(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }

        Event event = eventOpt.get();
        List<EventRegistration> registrations = eventRegistrationRepository.findByEvent(event);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registrations.size(); i++) {
            EventRegistration reg = registrations.get(i);
            json.append("{");
            json.append("\"id\":\"").append(reg.getId()).append("\",");
            json.append("\"username\":\"").append(reg.getUser() != null ? reg.getUser().getUsername() : "")
                    .append("\",");
            json.append("\"registrationDate\":").append(reg.getRegistrationDate()).append(",");
            json.append("\"paymentStatus\":\"").append(reg.getPaymentStatus()).append("\",");
            json.append("\"attendanceStatus\":\"").append(reg.getAttendanceStatus()).append("\",");
            json.append("\"teamName\":\"").append(reg.getTeamName() != null ? reg.getTeamName() : "").append("\"");
            json.append("}");
            if (i < registrations.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return ResponseEntity.ok(json.toString());
    }

    // Cancel registration
    public ResponseEntity<String> cancelRegistration(String eventId, String username) {
        if (eventId == null || eventId.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid event ID");
        }
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid username");
        }

        // Get event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event not found");
        }
        Event event = eventOpt.get();

        // Get user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Find registration
        EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, user);
        if (registration == null) {
            return ResponseEntity.status(404).body("Registration not found");
        }

        // Check if event has already started
        if (System.currentTimeMillis() > event.getEventDate()) {
            return ResponseEntity.status(400).body("Cannot cancel registration for past events");
        }

        // Remove registration
        eventRegistrationRepository.delete(registration);

        // Update participant count
        event.setCurrentParticipants(Math.max(0, event.getCurrentParticipants() - 1));
        event.setUpdatedAt(System.currentTimeMillis());
        eventRepository.save(event);

        return ResponseEntity.ok("{\"message\":\"Registration cancelled successfully\"}");
    }

    // Helper method to build event JSON
    private String buildEventJson(Event e) {
        int attendedCount = (e.getAttendedUsers() != null) ? e.getAttendedUsers().size() : 0;

        return "{" +
                "\"id\":\"" + e.getId() + "\"," +
                "\"eventName\":\"" + (e.getEventName() != null ? e.getEventName() : "") + "\"," +
                "\"description\":\"" + (e.getDescription() != null ? e.getDescription() : "") + "\"," +
                "\"eventDate\":" + e.getEventDate() + "," +
                "\"venue\":\"" + (e.getVenue() != null ? e.getVenue() : "") + "\"," +
                "\"registrationDeadline\":" + e.getRegistrationDeadline() + "," +
                "\"maxParticipants\":" + e.getMaxParticipants() + "," +
                "\"currentParticipants\":" + e.getCurrentParticipants() + "," +
                "\"attendedCount\":" + attendedCount + "," +
                "\"eventType\":\"" + (e.getEventType() != null ? e.getEventType() : "") + "\"," +
                "\"fee\":\"" + (e.getFee() != null ? e.getFee() : "0") + "\"," +
                "\"status\":\"" + (e.getStatus() != null ? e.getStatus() : "") + "\"," +
                "\"posterUrl\":\"" + (e.getPosterUrl() != null ? e.getPosterUrl() : "") + "\"," +
                "\"requirements\":\"" + (e.getRequirements() != null ? e.getRequirements() : "") + "\"," +
                "\"contactInfo\":\"" + (e.getContactInfo() != null ? e.getContactInfo() : "") + "\"," +
                "\"createdAt\":" + e.getCreatedAt() + "," +
                "\"updatedAt\":" + e.getUpdatedAt() +
                "}";
    }
}
