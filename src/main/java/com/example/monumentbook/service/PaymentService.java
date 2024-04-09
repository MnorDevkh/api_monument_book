package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.PaymentRequest;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<?> addPayment(PaymentRequest payment);
    ResponseEntity<?> findAllPayment();
}
