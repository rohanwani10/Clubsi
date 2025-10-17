package com.clubsi.clubsi.Repository;

import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRegistrationRepository extends MongoRepository<EventRegistration, String> {

    // Find all registrations for a specific event
    List<EventRegistration> findByEvent(Event event);

    // Find all registrations by a specific user
    List<EventRegistration> findByUser(User user);

    // Check if user is already registered for an event
    EventRegistration findByEventAndUser(Event event, User user);

    // Find registrations by payment status
    List<EventRegistration> findByPaymentStatus(String paymentStatus);

    // Find registrations by attendance status
    List<EventRegistration> findByAttendanceStatus(String attendanceStatus);

    // Count registrations for an event
    long countByEvent(Event event);
}
