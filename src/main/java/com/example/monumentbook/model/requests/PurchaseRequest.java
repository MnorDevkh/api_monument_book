package com.example.monumentbook.model.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequest {
    private int qty;
    private float cost;
    private float tax;
    private int invoice;
    private Integer supplier;
    private Integer book;
}
