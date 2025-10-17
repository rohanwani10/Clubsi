package com.clubsi.clubsi.Controller;

import com.clubsi.clubsi.Entity.PaymentLog;
import com.clubsi.clubsi.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/log")
    public ResponseEntity<String> logPayment(@RequestBody PaymentLog req) {
        return paymentService.logPayment(req);
    }

    @GetMapping("/getLog")
    public ResponseEntity<String> getPaymentLog(@RequestParam String transactionId) {
        return paymentService.getPaymentLog(transactionId);
    }

}
