package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.*;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.PurchaseDto;
import com.example.monumentbook.model.dto.PurchaseItemsDto;
import com.example.monumentbook.model.requests.SupplierRequest;
import com.example.monumentbook.model.responses.SupplierResponse;
import com.example.monumentbook.repository.*;
import com.example.monumentbook.service.SupplierService;
import com.example.monumentbook.utilities.response.ResponseObject;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final BookRepository bookRepository;
    private final PurchaseRepository purchaseRepository;
    private final BookSupplierRepository bookSupplierRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    @Override
    public ResponseEntity<?> addSupplier(SupplierRequest supplierRequest) {
        ResponseObject res = new ResponseObject();
        Supplier supplierResponse = Supplier.builder()
                .name(supplierRequest.getName())
                .image(supplierRequest.getImage())
                .phone(supplierRequest.getPhone())
                .email(supplierRequest.getEmail())
                .address(supplierRequest.getAddress())
                .date(LocalDate.now())
                .build();
        supplierRepository.save(supplierResponse);
        res.setMessage("Add successful");
        res.setData(supplierResponse);
        res.setStatus(true);
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> findAllSupplier() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<SupplierResponse> supplierResponses = new ArrayList<>();
            for (Supplier supplier : suppliers){
                Optional<Supplier> supplierOptional = supplierRepository.findById(supplier.getId());
                System.out.println(supplierOptional+" supplierOptional");
                if(supplierOptional.isPresent() && !supplierOptional.get().isDeleted()){
                    SupplierResponse supplierResponse = supplierResponse(supplierOptional.get());
                    supplierResponses.add(supplierResponse);
                }
            }
            ResponseObject res = new ResponseObject();
            res.setMessage("All suppliers fetched successfully");
            res.setData(supplierResponses);
            res.setStatus(true);
            return ResponseEntity.ok(res);
        }catch (Exception e){
            ResponseObject res = new ResponseObject();
            res.setMessage("All suppliers false");
            res.setData(null);
            res.setStatus(false);
            return ResponseEntity.ok(res);
        }

    }

    @Override
    public ResponseEntity<?> findSupplierById(Integer id) {
        try {
                Optional<Supplier> supplierOptional = supplierRepository.findById(id);
                ResponseObject res = new ResponseObject();
                if(supplierOptional.isPresent()){
                    SupplierResponse supplierResponse = supplierResponse(supplierOptional.get());
                    res.setMessage("All suppliers fetched successfully");
                    res.setData(supplierResponse);
                    res.setStatus(true);
                }

            return ResponseEntity.ok(res);
        }catch (Exception e){
            ResponseObject res = new ResponseObject();
            res.setMessage("All suppliers false");
            res.setData(null);
            res.setStatus(false);
            return ResponseEntity.ok(res);
        }
    }

    @Override
    public ResponseEntity<?> updateSupplier(Integer id, SupplierRequest supplierRequest) {
       try {
           ResponseObject res = new ResponseObject();
          Optional<Supplier> supplierOptional = supplierRepository.findById(id);
          if (supplierOptional.isPresent()){
               Supplier supplier = Supplier.builder()
                       .id(supplierOptional.get().getId())
                       .name(supplierRequest.getName())
                       .image(supplierRequest.getImage())
                       .phone(supplierRequest.getPhone())
                       .email(supplierRequest.getEmail())
                       .address(supplierRequest.getAddress())
                       .date(supplierOptional.get().getDate())
                       .build();
               supplierRepository.save(supplier);
              SupplierResponse supplierResponse = supplierResponse(supplier);
              res.setMessage("update suppliers fetched successfully");
              res.setData(supplierResponse);
              res.setStatus(true);
           }
           return ResponseEntity.ok(res);
       }catch (Exception e){
           ResponseObject res = new ResponseObject();
           res.setMessage("update suppliers false id not found");
           res.setData(null);
           res.setStatus(false);
           return ResponseEntity.ok(res);
       }
    }

    @Override
    public ResponseEntity<?> deleteSupplier(Integer id) {
        try {
            ResponseObject res = new ResponseObject();
            Optional<Supplier> supplierOptional = supplierRepository.findById(id);
            if (supplierOptional.isPresent()){
                Supplier supplier = Supplier.builder()
                        .id(supplierOptional.get().getId())
                        .name(supplierOptional.get().getName())
                        .image(supplierOptional.get().getImage())
                        .phone(supplierOptional.get().getPhone())
                        .email(supplierOptional.get().getEmail())
                        .address(supplierOptional.get().getAddress())
                        .date(supplierOptional.get().getDate())
                        .deleted(true)
                        .build();
                supplierRepository.save(supplier);
                SupplierResponse supplierResponse = supplierResponse(supplier);
                res.setMessage("delete suppliers successfully with id "+ supplier.getId());
                res.setData(null);
                res.setStatus(true);
            }
            return ResponseEntity.ok(res);
        }catch (Exception e){
            ResponseObject res = new ResponseObject();
            res.setMessage("delete suppliers not found with id" + id);
            res.setData(null);
            res.setStatus(false);
            return ResponseEntity.ok(res);
        }
    }

    private SupplierResponse supplierResponse(Supplier supplier) {
        List<SupplierResponse> supplierResponses = new ArrayList<>();
        List<PurchaseDto> purchaseDtoList = purchaseFlags(supplier);
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .image(supplier.getImage())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .purchaseDtos(purchaseDtoList)
                .date(LocalDate.now())
                .build();
    }
    private List<PurchaseDto> purchaseFlags(Supplier supplier){
        System.out.println(supplier+"supplier");
        List<Purchase> purchaseList = purchaseRepository.findAllBySupplier(supplier);
        List<PurchaseDto> purchaseDtoList = new ArrayList<>();

        for(Purchase purchase : purchaseList){
            Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchase.getId());
            if(purchaseOptional.isPresent()){
              List<PurchaseItemsDto> purchaseItemsDto = purchaseItemFlags(purchaseOptional.get());
              PurchaseDto purchaseDto = PurchaseDto.builder()
                      .id(purchaseOptional.get().getId())
                      .tax(purchaseOptional.get().getTax())
                      .invoice(purchaseOptional.get().getInvoice())
                      .purchaseItemsDto(purchaseItemsDto)
                      .build();
              purchaseDtoList.add(purchaseDto);
            }
        }
        return purchaseDtoList;
    }

    private List<PurchaseItemsDto> purchaseItemFlags(Purchase purchase) {
        List<PurchaseItemsDto> purchaseItemsDtoList = new ArrayList<>();
        try {
            List<PurchaseItems> purchaseItemsList = purchaseItemRepository.findByPurchaseId(purchase.getId());
            for (PurchaseItems purchaseItems : purchaseItemsList) {
                Optional<PurchaseItems> optionalPurchaseItems = purchaseItemRepository.findById(purchaseItems.getId());
                optionalPurchaseItems.ifPresent(item -> {
                    PurchaseItemsDto purchaseItemsDtoObj = PurchaseItemsDto.builder()
                            .id(item.getId())
                            .qty(item.getQty())
                            .cost(item.getCost())
                            .book(item.getBook().toDto())
                            .build();
                    purchaseItemsDtoList.add(purchaseItemsDtoObj);
                });
            }
        } catch (Exception e) {
            // Handle exception appropriately, maybe log it
            e.printStackTrace();
        }
        return purchaseItemsDtoList;
    }

//    private BookDto bookFlags(Purchase purchase){
//
//            Optional<Book> bookOptional = bookRepository.findById(purchase.getBook().getId());
//            if(bookOptional.isPresent()) {
//                BookDto bookDto = BookDto.builder()
//                        .id(bookOptional.get().getId())
//                        .title(bookOptional.get().getTitle())
//                        .isbn(bookOptional.get().getIsbn())
//                        .coverImg(bookOptional.get().getCoverImg())
//                        .description(bookOptional.get().getDescription()).price(bookOptional.get().getPrice())
//                        .qty(bookOptional.get().getQty())
//                        .publishDate(bookOptional.get().getPublishDate())
//                        .publisher(bookOptional.get().getPublisher())
//                        .build();
//                return bookDto;
//            }
//        return null;
//    }

}
