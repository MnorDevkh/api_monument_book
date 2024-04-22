package com.example.monumentbook.controller;

import com.example.monumentbook.model.requests.PurchaseRequest;
import com.example.monumentbook.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/v1/purchase")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;
    @GetMapping("/getAllPurchase")
    @Operation(summary = "get all vendor")
    public ResponseEntity<?> getAllImport(){
    return purchaseService.getAllPurchase();
}
    @DeleteMapping("/deletePurchase")
    @Operation(summary = "delete import by vendor id")
    public ResponseEntity<?> deleteImport(@Param(value = "vendor id")Integer id){
//        return bookService.deletePurchase(id);
        return null;
    }
    @PutMapping("/updatePurchase")
    @Operation(summary = "update import by vendor id")
    public ResponseEntity<?> UpdateImport(@Param(value = "vendor id")Integer id, @RequestBody PurchaseRequest purchaseRequest){
//        return bookService.updatePurchase(id, purchaseRequest);
        return purchaseService.updatePurchase(id,purchaseRequest);
    }
    @GetMapping("/getPurchaseById")
    @Operation(summary = "get import vendor by id")
    public ResponseEntity<?> getImportById(@Param("import vendor id") Integer id){
//        return bookService.getPurchaseById(id);
       return purchaseService.getPurchaseById(id);
    }
    @PostMapping("/addPurchase")
    @Operation(summary = "add product by Id")
    public ResponseEntity<?> addProductByName(@RequestBody PurchaseRequest purchaseRequest){
        return purchaseService.addPurchase(purchaseRequest);
    }

}
