package com.example.monumentbook.model.responses;

import com.example.monumentbook.enums.Action;
import com.example.monumentbook.model.OrderItem;
import com.example.monumentbook.model.User;
import com.example.monumentbook.model.dto.OrderItemDto;
import com.example.monumentbook.model.dto.PaymentDto;
import com.example.monumentbook.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Integer id;
    private List<OrderItemDto> orderItem;
    private LocalDate date;
    private boolean deleted;
    private boolean type;
    private Action action;
    private PaymentDto paymentDto;
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;
}
