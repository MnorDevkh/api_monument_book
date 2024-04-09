package com.example.monumentbook.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Setter
@Getter
@Table(name = "payment_tb")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String name;
    private String number;
    private Integer cvv ;
    private LocalDate date;

}
