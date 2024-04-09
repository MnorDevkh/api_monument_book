package com.example.monumentbook.model;

import com.example.monumentbook.enums.Action;
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
    private int qty;
    private float price;
    private LocalDate date;
    private boolean deleted;
    private boolean type;
    @OneToOne(fetch = FetchType.LAZY )
    private Payment payment;
    private String address;
    private Action  action;
}
