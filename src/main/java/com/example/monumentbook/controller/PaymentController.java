package com.example.monumentbook.controller;

import com.example.monumentbook.model.requests.PaymentRequest;
import com.example.monumentbook.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/v1/payment")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/all")
    public ResponseEntity<?> getPayment(){
        return paymentService.findAllPayment();
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody PaymentRequest paymentRequest){
        return paymentService.addPayment(paymentRequest);
    }
}
