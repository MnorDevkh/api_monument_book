package com.example.monumentbook.model.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequest {
    private float tax;
    private String invoice;
    private Integer supplier;
    private List<PurchaseItemRequest> itemObj;
}
