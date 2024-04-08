package com.example.monumentbook.model.responses;

import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private Integer id;
    private BookDto book;
    private int qty;
    private float price;
    private LocalDate date;
}
