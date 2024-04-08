package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.OrderRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    ResponseEntity<?> allCustomerOrder(Integer page, Integer size);
    ResponseEntity<?> allCurrentOrder(Integer page, Integer size);

    ResponseEntity<?> newOrder(List<OrderRequest> orderRequests);
}
