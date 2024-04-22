package com.example.monumentbook.model.responses;

import com.example.monumentbook.model.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseResponseItem {
    private int qty;
    private float cost;
    private BookDto book;
}
