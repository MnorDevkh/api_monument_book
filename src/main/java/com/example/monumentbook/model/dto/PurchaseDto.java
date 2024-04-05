package com.example.monumentbook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDto {
    private Integer id;
    private int qty;
    private float cost;
    private Integer invoice;
    private float tax;
    private BookDto book;
}
