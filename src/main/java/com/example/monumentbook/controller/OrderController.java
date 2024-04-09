package com.example.monumentbook.controller;

import com.example.monumentbook.model.Order;
import com.example.monumentbook.model.requests.OrderItemRequest;
import com.example.monumentbook.model.requests.OrderRequest;
import com.example.monumentbook.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/v1/order")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrder(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
        return orderService.allOrder(page, size);
    }
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        return orderService.allCurrentOrder(page, size);
    }
    @PostMapping("/new-order")
    public ResponseEntity<?> newOrder(@RequestBody  OrderRequest orderRequest){
        return orderService.newOrder(orderRequest);
    }
    @PutMapping("/confirm")
    public ResponseEntity<?> conform(@Param("order id") Integer id){
        return orderService.confirm(id);
    }
    @PutMapping("/reject")
    public ResponseEntity<?> reject(@Param("order id") Integer id){
        return orderService.reject(id);
    }

}
