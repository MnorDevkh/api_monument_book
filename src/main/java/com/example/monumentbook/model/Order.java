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
@Table(name = "Order_tb")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "userId")
    private User userId;
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "bookId")
    private Book bookId;
    private int qty;
    private float price;
    private LocalDate date;
    private boolean deleted;
    private boolean action;
}
