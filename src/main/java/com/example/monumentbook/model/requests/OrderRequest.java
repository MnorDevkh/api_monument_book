package com.example.monumentbook.model.requests;

import com.example.monumentbook.enums.Action;
import com.example.monumentbook.model.OrderItem;
import com.example.monumentbook.model.Payment;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<OrderItemRequest> orderItem;
    private int qty;
    private float price;
    private String phone;
    private Integer paymentId;
    private boolean type;
    private String address;

}
