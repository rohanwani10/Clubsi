package com.clubsi.clubsi.Controller;

import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // Create a new event
    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    // Get all events
    @GetMapping("/all")
    public ResponseEntity<String> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get event by ID
    @GetMapping("/{eventId}")
    public ResponseEntity<String> getEventById(@PathVariable String eventId) {
        return eventService.getEventById(eventId);
    }

    // Get events by status (e.g., UPCOMING, ONGOING, COMPLETED, CANCELLED)
    @GetMapping("/status/{status}")
    public ResponseEntity<String> getEventsByStatus(@PathVariable String status) {
        return eventService.getEventsByStatus(status);
    }

    // Update event
    @PutMapping("/update/{eventId}")
    public ResponseEntity<String> updateEvent(@PathVariable String eventId, @RequestBody Event event) {
        return eventService.updateEvent(eventId, event);
    }

    // Delete event
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) {
        return eventService.deleteEvent(eventId);
    }

    // Register for an event
    @PostMapping("/register/{eventId}")
    public ResponseEntity<String> registerForEvent(@PathVariable String eventId,
            @RequestBody EventRegistration registration) {
        return eventService.registerForEvent(eventId, registration);
    }

    // Get user's registered events
    @GetMapping("/user/{username}/registrations")
    public ResponseEntity<String> getUserRegistrations(@PathVariable String username) {
        return eventService.getUserRegistrations(username);
    }

    // Mark attendance for an event
    @PostMapping("/{eventId}/attendance/{username}")
    public ResponseEntity<String> markAttendance(@PathVariable String eventId, @PathVariable String username) {
        return eventService.markAttendance(eventId, username);
    }

    // Get attended users for an event
    @GetMapping("/{eventId}/attended")
    public ResponseEntity<String> getAttendedUsers(@PathVariable String eventId) {
        return eventService.getAttendedUsers(eventId);
    }

    // Get all registrations for a specific event
    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<String> getEventRegistrations(@PathVariable String eventId) {
        return eventService.getEventRegistrations(eventId);
    }

    // Cancel registration
    @DeleteMapping("/{eventId}/cancel/{username}")
    public ResponseEntity<String> cancelRegistration(@PathVariable String eventId, @PathVariable String username) {
        return eventService.cancelRegistration(eventId, username);
    }
}
