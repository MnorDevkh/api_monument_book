package com.example.monumentbook.model.responses;

import com.example.monumentbook.model.dto.PurchaseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SupplierResponse {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String image;
    private LocalDate date;
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PurchaseDto> purchaseDtos;

}
