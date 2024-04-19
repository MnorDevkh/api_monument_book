package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.OrderItemRequest;
import com.example.monumentbook.model.requests.PurchaseRequest;
import com.example.monumentbook.model.requests.BookRequest;
import com.example.monumentbook.model.requests.RequestById;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookService {
    ResponseEntity<?> findAllBook(Integer pageNumber, Integer pageSize, String sortBy, boolean ascending);
    ResponseEntity<?> findBookById(Integer id);
    ResponseEntity<?> saveBook(BookRequest book);
    ResponseEntity<?> updateBook(BookRequest book, Integer id);
    ResponseEntity<?> DeleteById(Integer id);
    ResponseEntity<?> getBookOfTheWeek(Integer page, Integer size);
    ResponseEntity<?> getBestSell(Integer page, Integer size);
    ResponseEntity<?> getNewArrival(Integer page, Integer size);
    ResponseEntity<?> addBookOfTheWeek(RequestById requestById);
    ResponseEntity<?> addBestSell(RequestById requestById);
    ResponseEntity<?> addNewArrival(RequestById requestById);
    ResponseEntity<?> deleteBookOfTheWeek(RequestById requestById);
    ResponseEntity<?> deleteBestSell(RequestById requestById);
    ResponseEntity<?> deleteNewArrival(RequestById requestById);
    ResponseEntity<?> getAllPurchase();
    ResponseEntity<?> deletePurchase(Integer id);
    ResponseEntity<?> processCheckoutById(List<OrderItemRequest> orderItemRequest);
    ResponseEntity<?> updatePurchase(Integer id, PurchaseRequest purchaseRequest);
    ResponseEntity<?> purchaseById(PurchaseRequest purchaseRequest);
    ResponseEntity<?> getPurchaseById(Integer id);
}
