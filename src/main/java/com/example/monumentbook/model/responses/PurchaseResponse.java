package com.example.monumentbook.model.responses;

import com.example.monumentbook.model.PurchaseItems;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.PurchaseItemsDto;
import com.example.monumentbook.model.dto.SupplierDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseResponse {
    private Integer id;
    private String invoice;
    private float tax;
    private LocalDate date;
    private SupplierDto supplier;
    private List<PurchaseItemsDto> purchaseItems;

}
