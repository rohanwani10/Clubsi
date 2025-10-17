package com.clubsi.clubsi.Service;

import com.clubsi.clubsi.Entity.PaymentLog;
import com.clubsi.clubsi.Entity.User;
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

    public ResponseEntity<String> getPaymentLog(String transactionId) {
        if (transactionId == null || transactionId.isEmpty())
            return ResponseEntity.status(400).body("Invalid transaction ID");

        PaymentLog log = paymentLogRepository.findByTransactionId(transactionId);

        if (log == null)
            return ResponseEntity.status(404).body("Payment log not found");

        User user = log.getUser();

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
                + "\"phone\":\"" + (user != null ? user.getPhone() : "") + "\","
                + "\"email\":\"" + (user != null ? user.getEmail() : "") + "\""
                + "}";
        return ResponseEntity.ok(resp);
    }

    public ResponseEntity<String> logPayment(PaymentLog paymentLog) {
        if (paymentLog == null)
            return ResponseEntity.status(400).body("Invalid payment log");

        // The client must provide a user object with at least username or id
        if (paymentLog.getUser() == null) {
            return ResponseEntity.status(400).body("User information is required");
        }

        User user = null;

        // Try to find user by username if provided
        if (paymentLog.getUser().getUsername() != null && !paymentLog.getUser().getUsername().isEmpty()) {
            user = userRepository.findByUsername(paymentLog.getUser().getUsername());
        }
        // Or try to find by id if provided
        else if (paymentLog.getUser().getId() != null && !paymentLog.getUser().getId().isEmpty()) {
            user = userRepository.findById(paymentLog.getUser().getId()).orElse(null);
        }

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Set the full User object as DBRef (Spring Data will handle the reference)
        paymentLog.setUser(user);

        PaymentLog saved = paymentLogRepository.save(paymentLog);

        String resp = "{\"id\":\"" + saved.getId() + "\","
                + "\"transactionId\":\"" + saved.getTransactionId() + "\"}";
        return ResponseEntity.ok(resp);
    }

}
