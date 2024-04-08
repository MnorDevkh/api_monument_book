package com.example.monumentbook.model.dto;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
    private Integer id;
    private BookDto book;
    private int qty;
    private float price;
}
