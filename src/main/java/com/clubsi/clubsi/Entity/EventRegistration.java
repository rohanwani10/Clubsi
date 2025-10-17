package com.clubsi.clubsi.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(collection = "event_registrations")
public class EventRegistration {

    @Id
    private String id;

    @DBRef
    private Event event;

    @DBRef
    private User user;

    private long registrationDate; // timestamp when user registered
    private String paymentStatus; // "PENDING", "COMPLETED", "FAILED"
    private String attendanceStatus; // "REGISTERED", "ATTENDED", "ABSENT"

    // Payment reference if event has a fee
    @DBRef
    private PaymentLog paymentLog;

    // Additional registration data
    private String remarks; // any special requirements/notes from user
    private String teamName; // if it's a team event
    private int teamSize; // number of team members

    @Override
    public String toString() {
        return "EventRegistration{" +
                "id='" + id + '\'' +
                ", event=" + (event != null ? event.getEventName() : "null") +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", registrationDate=" + registrationDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", attendanceStatus='" + attendanceStatus + '\'' +
                ", teamName='" + teamName + '\'' +
                ", teamSize=" + teamSize +
                '}';
    }
}
