package com.example.monumentbook.model.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Integer id;
    private String name;
    private String number;
    private String type;
    private Integer cvv;
    private LocalDate date;
}
