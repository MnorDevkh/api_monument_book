package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.Purchase;
import com.example.monumentbook.model.PurchaseItems;
import com.example.monumentbook.model.Supplier;
import com.example.monumentbook.model.dto.PurchaseItemsDto;
import com.example.monumentbook.model.requests.PurchaseItemRequest;
import com.example.monumentbook.model.requests.PurchaseRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.PurchaseResponse;
import com.example.monumentbook.repository.BookRepository;
import com.example.monumentbook.repository.PurchaseRepository;
import com.example.monumentbook.repository.PurchaseItemRepository;
import com.example.monumentbook.repository.SupplierRepository;
import com.example.monumentbook.service.PurchaseService;
import com.example.monumentbook.utilities.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final BookRepository bookRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public ResponseEntity<?> getAllPurchase(Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").descending());
            Page<Purchase> pageResult = purchaseRepository.findAll(pageable);
            List<Purchase> purchaseList = purchaseRepository.findAll();
            List<PurchaseResponse> purchaseResponses = new ArrayList<>();
            for (Purchase purchases : purchaseList) {
                Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchases.getId());
                if (purchaseOptional.isPresent()) {
                    PurchaseResponse purchasedResponse = purchaseResponse(purchaseOptional.get());
                    purchaseResponses.add(purchasedResponse);
                }
            }
//            ResponseObject res = new ResponseObject();
//            res.setMessage("All purchases found");
//            res.setStatus(true);
//            res.setData(purchaseResponses);
            ApiResponse res = new ApiResponse(true, "Fetch Purchase successful!", purchaseResponses, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseObject res = new ResponseObject();
            res.setMessage("Failed to fetch all purchases");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> deletePurchase(Integer id) {
        try {
            List<PurchaseItems> purchaseItemsByPurchase = purchaseItemRepository.findByPurchaseId(id);
            System.out.println("purchaseItemsByPurchase" + purchaseItemsByPurchase);
            for (PurchaseItems purchaseItems : purchaseItemsByPurchase) {
                Optional<PurchaseItems> purchaseItemsOptional = purchaseItemRepository.findById(purchaseItems.getId());
                if (purchaseItemsOptional.isPresent()) {
                    purchaseItemRepository.delete(purchaseItemsOptional.get());
                    Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
                    if (purchaseOptional.isPresent()) {
                        purchaseRepository.delete(purchaseOptional.get());
                    }

                    Optional<Book> bookOptional = bookRepository.findById(purchaseItemsOptional.get().getBook().getId());
                    if (bookOptional.isPresent()) {
                        Book book = Book.builder()
                                .id(bookOptional.get().getId())
                                .isbn(bookOptional.get().getIsbn())
                                .title(bookOptional.get().getTitle())
                                .description(bookOptional.get().getDescription())
                                .coverImg(bookOptional.get().getCoverImg())
                                .publisher(bookOptional.get().getPublisher())
                                .publishDate(bookOptional.get().getPublishDate())
                                .price(bookOptional.get().getPrice())
                                .qty(bookOptional.get().getQty() - purchaseItemsOptional.get().getQty())
                                .deleted(bookOptional.get().isDeleted())
                                .bestSell(bookOptional.get().isBestSell())
                                .newArrival(bookOptional.get().isNewArrival())
                                .ofTheWeek(bookOptional.get().isOfTheWeek())
                                .build();
                        bookRepository.save(book);
                    }
                }

            }


            return ResponseEntity.ok("delete success with id" + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getPurchaseById(Integer id) {
        ResponseObject res = new ResponseObject();
        try {
            Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
            PurchaseResponse purchaseResponse = purchaseResponse(purchaseOptional.get());
            res.setMessage("add purchase successful");
            res.setStatus(true);
            res.setData(purchaseResponse);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setMessage("Failed to fetch all purchases");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> updatePurchase(Integer id, PurchaseRequest purchaseRequest) {
        try {
            Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
            ResponseObject res = new ResponseObject();
            if (purchaseOptional.isPresent()) {
                Optional<Supplier> supplierOptional = supplierRepository.findById(purchaseRequest.getSupplier());

                if (supplierOptional.isPresent()) {
                    Purchase purchase = Purchase.builder()
                            .id(id)
                            .tax(purchaseRequest.getTax())
                            .invoice(purchaseRequest.getInvoice())
                            .supplier(supplierOptional.get())
                            .date(LocalDate.now())
                            .build();
                    purchaseRepository.save(purchase);
                    Purchase purchase1 = Purchase.builder()
                            .id(purchase.getId())
                            .build();
                    List<PurchaseItemsDto> purchaseItemsDtoList = new ArrayList<>();
                    for (PurchaseItemRequest purchaseItemRequest : purchaseRequest.getItemObj()) {
                        Optional<Book> bookOptional = bookRepository.findById(purchaseItemRequest.getBookId());
                        if (bookOptional.isPresent()) {
                            List<PurchaseItems> purchaseItemsByPurchase = purchaseItemRepository.findByPurchaseId(id);
                            System.out.println("purchaseItemsByPurchase" + purchaseItemsByPurchase);
                            for (PurchaseItems purchaseItems : purchaseItemsByPurchase) {
                                System.out.println("purchaseItems" + purchaseItems);
                                Optional<PurchaseItems> purchaseItemsOptional = purchaseItemRepository.findById(purchaseItems.getId());
                                if (purchaseItemsOptional.isPresent()) {
                                    System.out.println("purchaseItemsOptional" + purchaseItemsOptional.get());
                                    purchaseItemRepository.delete(purchaseItemsOptional.get());
                                }
                                PurchaseItems purchaseItemsObj = PurchaseItems.builder()
                                        .qty(purchaseItemRequest.getQty())
                                        .cost(purchaseItemRequest.getCost())
                                        .book(bookOptional.get())
                                        .purchase(purchase1)
                                        .build();
                                purchaseItemRepository.save(purchaseItemsObj);
                                Book book = Book.builder()
                                        .id(bookOptional.get().getId())
                                        .isbn(bookOptional.get().getIsbn())
                                        .title(bookOptional.get().getTitle())
                                        .description(bookOptional.get().getDescription())
                                        .coverImg(bookOptional.get().getCoverImg())
                                        .publisher(bookOptional.get().getPublisher())
                                        .publishDate(bookOptional.get().getPublishDate())
                                        .price(bookOptional.get().getPrice())
                                        .qty(bookOptional.get().getQty() + purchaseItemRequest.getQty() - purchaseItemsOptional.get().getQty())
                                        .deleted(bookOptional.get().isDeleted())
                                        .bestSell(bookOptional.get().isBestSell())
                                        .newArrival(bookOptional.get().isNewArrival())
                                        .ofTheWeek(bookOptional.get().isOfTheWeek())
                                        .build();
                                bookRepository.save(book);
                                ResponseEntity<?> purchaseResponseItem = purchaseResponseItem(purchaseItemsObj);
                                PurchaseItemsDto purchaseItemsDto = (PurchaseItemsDto) purchaseResponseItem.getBody();
                                purchaseItemsDtoList.add(purchaseItemsDto);
                            }

                        }
                        Optional<Purchase> purchases = purchaseRepository.findById(purchase.getId());
                        PurchaseResponse purchaseResponse = purchaseResponse(purchases.get());
                        res.setMessage("add purchase successful");
                        res.setStatus(true);
                        res.setData(purchaseResponse);

                    }
                }

            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<?> addPurchase(PurchaseRequest purchaseRequest) {
        try {
            Optional<Supplier> supplierOptional = supplierRepository.findById(purchaseRequest.getSupplier());
            ResponseObject res = new ResponseObject();
            if (supplierOptional.isPresent()) {
                Purchase purchase = Purchase.builder()
                        .tax(purchaseRequest.getTax())
                        .invoice(purchaseRequest.getInvoice())
                        .supplier(supplierOptional.get())
                        .date(LocalDate.now())
                        .build();
                purchaseRepository.save(purchase);
                Purchase purchase1 = Purchase.builder()
                        .id(purchase.getId())
                        .build();
                List<PurchaseItemsDto> purchaseItemsDtoList = new ArrayList<>();
                for (PurchaseItemRequest purchaseItemRequest : purchaseRequest.getItemObj()) {
                    Optional<Book> bookOptional = bookRepository.findById(purchaseItemRequest.getBookId());
                    if (bookOptional.isPresent()) {
                        PurchaseItems purchaseItemsObj = PurchaseItems.builder()
                                .qty(purchaseItemRequest.getQty())
                                .cost(purchaseItemRequest.getCost())
                                .book(bookOptional.get())
                                .purchase(purchase1)
                                .build();
                        purchaseItemRepository.save(purchaseItemsObj);
                        Book book = Book.builder()
                                .id(bookOptional.get().getId())
                                .isbn(bookOptional.get().getIsbn())
                                .title(bookOptional.get().getTitle())
                                .description(bookOptional.get().getDescription())
                                .coverImg(bookOptional.get().getCoverImg())
                                .publisher(bookOptional.get().getPublisher())
                                .publishDate(bookOptional.get().getPublishDate())
                                .price(bookOptional.get().getPrice())
                                .qty(bookOptional.get().getQty() + purchaseItemRequest.getQty())
                                .deleted(bookOptional.get().isDeleted())
                                .bestSell(bookOptional.get().isBestSell())
                                .newArrival(bookOptional.get().isNewArrival())
                                .ofTheWeek(bookOptional.get().isOfTheWeek())
                                .build();
                        bookRepository.save(book);
                        ResponseEntity<?> purchaseResponseItem = purchaseResponseItem(purchaseItemsObj);
                        PurchaseItemsDto purchaseItemsDto = (PurchaseItemsDto) purchaseResponseItem.getBody();
                        purchaseItemsDtoList.add(purchaseItemsDto);

                    }
                }
                Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchase.getId());
                PurchaseResponse purchaseResponse = purchaseResponse(purchaseOptional.get());
                res.setMessage("add purchase successful");
                res.setStatus(true);
                res.setData(purchaseResponse);

            }


            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private PurchaseResponse purchaseResponse(Purchase purchases) {
        List<PurchaseItems> purchaseItemsList = purchaseItemRepository.findByPurchaseId(purchases.getId());
        List<PurchaseItemsDto> purchaseItemsDtoList = new ArrayList<>();
        for (PurchaseItems purchaseItems : purchaseItemsList) {
            Optional<PurchaseItems> purchaseItemsOptional = purchaseItemRepository.findById(purchaseItems.getId());
            if (purchaseItemsOptional.isPresent()) {
                PurchaseItemsDto purchaseItemsDto = PurchaseItemsDto.builder()
                        .id(purchaseItemsOptional.get().getId())
                        .qty(purchaseItemsOptional.get().getQty())
                        .cost(purchaseItemsOptional.get().getCost())
                        .book(purchaseItemsOptional.get().getBook().toDto())
                        .build();
                purchaseItemsDtoList.add(purchaseItemsDto);
            }

        }
        return PurchaseResponse.builder()
                .id(purchases.getId())
                .date(purchases.getDate())
                .tax(purchases.getTax())
                .supplier(purchases.getSupplier().toDto())
                .invoice(purchases.getInvoice())
                .purchaseItems(purchaseItemsDtoList)
                .build();
    }


    private ResponseEntity<?> purchaseResponseItem(PurchaseItems purchaseItemsObj) {
        try {
            PurchaseItemsDto purchaseItemsDto = PurchaseItemsDto.builder()
                    .id(purchaseItemsObj.getId())
                    .qty(purchaseItemsObj.getQty())
                    .cost(purchaseItemsObj.getCost())
                    .book(purchaseItemsObj.getBook().toDto())
                    .build();
            return ResponseEntity.ok(purchaseItemsDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

