package com.example.monumentbook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDto {
    private Integer id;
    private String invoice;
    private float tax;
    private List<PurchaseItemsDto> purchaseItemsDto;
}
