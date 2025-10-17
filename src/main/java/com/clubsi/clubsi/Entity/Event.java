package com.clubsi.clubsi.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String eventName;
    private String description;
    private long eventDate; // timestamp
    private String venue;
    private long registrationDeadline; // timestamp
    private int maxParticipants;
    private int currentParticipants;

    private String eventType; // e.g., "TECHNICAL", "CULTURAL", "SPORTS", "WORKSHOP"
    private String fee; // event fee (could be "0" for free events)
    private String status; // e.g., "UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"

    private long createdAt;
    private long updatedAt;

    // Image URL or poster
    private String posterUrl;

    // Additional event details
    private String requirements; // what participants need to bring/prepare
    private String contactInfo; // contact details for queries

    @DBRef
    private LinkedList<User> attendedUsers = new LinkedList<>();

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", venue='" + venue + '\'' +
                ", registrationDeadline=" + registrationDeadline +
                ", maxParticipants=" + maxParticipants +
                ", currentParticipants=" + currentParticipants +

                ", eventType='" + eventType + '\'' +
                ", fee='" + fee + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }
}
