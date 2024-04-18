package com.example.monumentbook.model;

import com.example.monumentbook.model.dto.CreditCardDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@ToString(exclude = "user")
@Table(name = "credit_card_tb")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String fullName;
    @Column(unique = true)
    private String cardNumber;
    private int cvv;
    @Column(name = "expiry_month")
    private int expiryMonth;
    @Column(name = "expiry_year")
    private int expiryYear;
    private String address;
    private String city;
    private LocalDate date;
    @ManyToOne (fetch = FetchType.LAZY )
    @JoinColumn(name = "userId")
    private User user;
    public CreditCardDto toDto(){
       return  new CreditCardDto(this.id,this.fullName,this.cardNumber,this.cvv, this.expiryMonth, this.expiryYear,this.address,this.city);

    }

}
