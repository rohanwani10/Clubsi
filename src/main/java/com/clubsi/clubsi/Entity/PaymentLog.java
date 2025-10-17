package com.clubsi.clubsi.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(collection = "payment_logs")
public class PaymentLog {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionId;
    private String paymentMethodToken;
    private String totalPrice;
    private String currencyCode;
    private String paymentStatus;
    private String errorMessage;
    private long timestamp;

    @DBRef
    private Event event;

    @DBRef
    private User user;

    @Override
    public String toString() {
        return "PaymentLog{" +
                "id='" + id + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentMethodToken='" + paymentMethodToken + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", timestamp=" + timestamp +
                ", user='" + user + '\'' +
                '}';
    }
}
