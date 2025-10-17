package com.clubsi.clubsi.Repository;

import com.clubsi.clubsi.Entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    // Find events by status
    List<Event> findByStatus(String status);

    // Find events by type
    List<Event> findByEventType(String eventType);

    // Find events with registration still open (deadline not passed)
    List<Event> findByRegistrationDeadlineGreaterThan(long currentTime);

    // Find events by name (partial match)
    List<Event> findByEventNameContainingIgnoreCase(String eventName);
}
