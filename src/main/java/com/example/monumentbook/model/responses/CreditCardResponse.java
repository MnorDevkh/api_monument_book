package com.example.monumentbook.model.responses;

import com.example.monumentbook.model.User;
import com.example.monumentbook.model.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreditCardResponse {
    private Integer id;
    private String fullName;
    private String cardNumber;
    private int cvv;
    private int expiryMonth;
    private int expiryYear;
    private String address;
    private String city;
    private LocalDate date;
    private UserDto user;
}
