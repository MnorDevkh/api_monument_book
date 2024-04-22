package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.PurchaseRequest;
import org.springframework.http.ResponseEntity;

public interface PurchaseService {
    ResponseEntity<?> getAllPurchase();
    ResponseEntity<?> deletePurchase(Integer id);
    ResponseEntity<?> getPurchaseById(Integer id);
    ResponseEntity<?> updatePurchase(Integer id, PurchaseRequest purchaseRequest);
    ResponseEntity<?> addPurchase(PurchaseRequest purchaseRequest);
}
