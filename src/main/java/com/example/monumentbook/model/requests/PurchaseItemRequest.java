package com.example.monumentbook.model.requests;

import com.example.monumentbook.model.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseItemRequest {
    private int qty;
    private float cost;
    private Integer bookId;
}
