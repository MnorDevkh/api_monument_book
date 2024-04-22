package com.example.monumentbook.model.dto;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.Purchase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemsDto {
    private Integer id;
    private int qty;
    private float cost;
    private BookDto book;
}
