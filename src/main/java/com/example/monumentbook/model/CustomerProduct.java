package com.example.monumentbook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CustomerProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String customer_name;
    private String phoneNumber;
    private long customer_id;
    private String product_name;
    private Integer product_id;
    private int qty;
    private int price;
    private LocalDate date;
}
