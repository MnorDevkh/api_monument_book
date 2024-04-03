package com.example.monumentbook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCardDto {

    private Integer id;
    private String fullName;
    private long cardNumber;
    private int cvv;
    private int expiryMonth;
    private int expiryYear;
    private String address;
    private String city;

}
