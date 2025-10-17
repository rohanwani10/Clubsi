package com.clubsi.clubsi.Service;

import com.clubsi.clubsi.Entity.Event;
import com.clubsi.clubsi.Entity.EventRegistration;
import com.clubsi.clubsi.Entity.PaymentLog;
import com.clubsi.clubsi.Entity.User;
import com.clubsi.clubsi.Repository.EventRegistrationRepository;
import com.clubsi.clubsi.Repository.EventRepository;
import com.clubsi.clubsi.Repository.PaymentLogRepository;
import com.clubsi.clubsi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    public ResponseEntity<String> getPaymentLog(String transactionId) {
        if (transactionId == null || transactionId.isEmpty())
            return ResponseEntity.status(400).body("Invalid transaction ID");

        PaymentLog log = paymentLogRepository.findByTransactionId(transactionId);

        if (log == null)
            return ResponseEntity.status(404).body("Payment log not found");

        // The DBRef User and Event are automatically loaded by Spring Data MongoDB
        User user = log.getUser();
        Event event = log.getEvent();

        String resp = "{\"id\":\"" + log.getId() + "\","
                + "\"transactionId\":\"" + log.getTransactionId() + "\","
                + "\"paymentMethodToken\":\"" + (log.getPaymentMethodToken() != null ? log.getPaymentMethodToken() : "")
                + "\","
                + "\"totalPrice\":\"" + (log.getTotalPrice() != null ? log.getTotalPrice() : "") + "\","
                + "\"currencyCode\":\"" + (log.getCurrencyCode() != null ? log.getCurrencyCode() : "") + "\","
                + "\"paymentStatus\":\"" + (log.getPaymentStatus() != null ? log.getPaymentStatus() : "") + "\","
                + "\"errorMessage\":\"" + (log.getErrorMessage() != null ? log.getErrorMessage() : "") + "\","
                + "\"timestamp\":" + log.getTimestamp() + ","
                + "\"username\":\"" + (user != null ? user.getUsername() : "") + "\","
                + "\"phone\":\"" + (user != null && user.getPhone() != null ? user.getPhone() : "") + "\","
                + "\"email\":\"" + (user != null && user.getEmail() != null ? user.getEmail() : "") + "\","
                + "\"eventName\":\"" + (event != null && event.getEventName() != null ? event.getEventName() : "")
                + "\","
                + "\"eventId\":\"" + (event != null && event.getId() != null ? event.getId() : "") + "\""
                + "}";
        return ResponseEntity.ok(resp);
    }

    public ResponseEntity<String> logPayment(PaymentLog paymentLog) {
        if (paymentLog == null)
            return ResponseEntity.status(400).body("Invalid payment log");

        // Validate and resolve user
        if (paymentLog.getUser() == null) {
            return ResponseEntity.status(400).body("User information is required");
        }

        User user = resolveUser(paymentLog.getUser());
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        paymentLog.setUser(user);

        // Validate and resolve event if provided
        if (paymentLog.getEvent() != null) {
            Event event = resolveEvent(paymentLog.getEvent());
            if (event == null) {
                return ResponseEntity.status(404).body("Event not found");
            }

            // Validate payment amount matches event fee
            if (event.getFee() != null && !event.getFee().equals("0")) {
                if (paymentLog.getTotalPrice() == null || !paymentLog.getTotalPrice().equals(event.getFee())) {
                    return ResponseEntity.status(400).body("Payment amount does not match event fee");
                }
            }

            paymentLog.setEvent(event);

            // Update event registration payment status if registration exists
            EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, user);
            if (registration != null && "SUCCESS".equalsIgnoreCase(paymentLog.getPaymentStatus())) {
                registration.setPaymentStatus("COMPLETED");
                registration.setPaymentLog(paymentLog); // Link payment to registration
                eventRegistrationRepository.save(registration);
            }
        }

        // Set timestamp if not provided
        if (paymentLog.getTimestamp() == 0) {
            paymentLog.setTimestamp(System.currentTimeMillis());
        }

        // Validate transaction ID uniqueness
        if (paymentLog.getTransactionId() != null) {
            PaymentLog existing = paymentLogRepository.findByTransactionId(paymentLog.getTransactionId());
            if (existing != null) {
                return ResponseEntity.status(400).body("Transaction ID already exists");
            }
        }

        PaymentLog saved = paymentLogRepository.save(paymentLog);

        String resp = "{\"id\":\"" + saved.getId() + "\","
                + "\"transactionId\":\"" + saved.getTransactionId() + "\","
                + "\"paymentStatus\":\"" + saved.getPaymentStatus() + "\"}";
        return ResponseEntity.ok(resp);
    }

    // Helper method to resolve User from partial data
    private User resolveUser(User partialUser) {
        if (partialUser.getUsername() != null && !partialUser.getUsername().isEmpty()) {
            return userRepository.findByUsername(partialUser.getUsername());
        } else if (partialUser.getId() != null && !partialUser.getId().isEmpty()) {
            return userRepository.findById(partialUser.getId()).orElse(null);
        } else if (partialUser.getEmail() != null && !partialUser.getEmail().isEmpty()) {
            return userRepository.findByEmail(partialUser.getEmail());
        }
        return null;
    }

    // Helper method to resolve Event from partial data
    private Event resolveEvent(Event partialEvent) {
        if (partialEvent.getId() != null && !partialEvent.getId().isEmpty()) {
            return eventRepository.findById(partialEvent.getId()).orElse(null);
        }
        return null;
    }

}
