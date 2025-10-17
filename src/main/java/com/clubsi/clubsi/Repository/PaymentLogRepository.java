package com.clubsi.clubsi.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.clubsi.clubsi.Entity.PaymentLog;

public interface PaymentLogRepository extends MongoRepository<PaymentLog, String> {
    PaymentLog findByTransactionId(String transactionId);
}
