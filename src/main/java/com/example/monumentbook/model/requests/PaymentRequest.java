package com.example.monumentbook.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String name;
    private String number;
    private Integer cvv ;

}
