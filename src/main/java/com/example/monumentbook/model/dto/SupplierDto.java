package com.example.monumentbook.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {
    private Integer id;
    private String name;
    private String image;
    private String address;
    private String phone;
    private String email;
}
