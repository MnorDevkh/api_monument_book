package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.CartRequest;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<?> addToCart(CartRequest cartRequest);
    ResponseEntity<?> getCart(Integer page, Integer size);
    ResponseEntity<?> getCartById(Integer id);
    ResponseEntity<?> updateCartById(Integer id, CartRequest cartRequest);
    ResponseEntity<?> deleteCartById(Integer id);
    ResponseEntity<?> findCartByUser(Integer page, Integer size);
}