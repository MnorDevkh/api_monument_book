package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.Payment;
import com.example.monumentbook.model.requests.PaymentRequest;
import com.example.monumentbook.model.responses.PaymentResponse;
import com.example.monumentbook.repository.PaymentRepository;
import com.example.monumentbook.service.PaymentService;
import com.example.monumentbook.utilities.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    @Override
    public ResponseEntity<?> addPayment(PaymentRequest paymentRequest) {
        Payment payment = Payment.builder()
                .cvv(paymentRequest.getCvv())
                .number(paymentRequest.getNumber())
                .name(paymentRequest.getName())
                .type(paymentRequest.getType())
                .date(LocalDate.now())
                .build();
        paymentRepository.save(payment);

    PaymentResponse paymentResponse = paymentResponseFlags(payment);
        ResponseObject res = new ResponseObject();
        res.setData(paymentResponse);
        res.setMessage("payment successful!");
        res.setStatus(true);
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> findAllPayment() {
        try {
        List<PaymentResponse> paymentList = new ArrayList<>();
        List<Payment> payments = paymentRepository.findAll();
        for (Payment payment: payments){
            Optional<Payment> paymentOptional = paymentRepository.findById(payment.getId());
            PaymentResponse paymentResponse = paymentResponseFlags(paymentOptional.get());
            paymentList.add(paymentResponse);
            }
            ResponseObject res = new ResponseObject();
            res.setData(paymentList);
            res.setMessage("payment successful!");
            res.setStatus(true);
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            return null;
        }

    }

    private PaymentResponse paymentResponseFlags(Payment payment){
        return PaymentResponse.builder()
                .cvv(payment.getCvv())
                .id(payment.getId())
                .number(payment.getNumber())
                .type(payment.getType())
                .name(payment.getName())
                .date(payment.getDate())
                .build();
    }
}
