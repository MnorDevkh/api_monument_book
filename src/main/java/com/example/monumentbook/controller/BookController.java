package com.example.monumentbook.controller;

import com.example.monumentbook.model.requests.BookRequest;
import com.example.monumentbook.model.requests.CustomerRequest;
import com.example.monumentbook.model.requests.ProductRequest;
import com.example.monumentbook.model.requests.RequestById;
import com.example.monumentbook.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/v1/book")
@SecurityRequirement(name = "bearerAuth")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getBook(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        return bookService.findAllBook(page,size);
    }
    @GetMapping("/getById")
    public ResponseEntity<?> getBookById(@Param("book id") Integer id){
        return bookService.findBookById(id);
    }
    @PostMapping("/add")
    @Operation(summary = "creat book")
    public ResponseEntity<?> addBook(@RequestBody BookRequest book){
        return bookService.saveBook(book);
    }

    @PutMapping("/update")
    @Operation(summary = "Update book")
    public ResponseEntity<?> updateBookById(@Param("book id") Integer id,@RequestBody BookRequest bookRequest){
        return bookService.updateBook(bookRequest,id);
    }
    @DeleteMapping("/deleteById")
    @Operation(summary = "delete book")
    public  ResponseEntity<?> deleteBookById(@Param("book id") Integer id){
        return  bookService.DeleteById(id);
    }

    @PostMapping("/add-productById")
    @Operation(summary = "add product by Id")
    public ResponseEntity<?> addProductByName(@Param("book id") Integer id, @RequestBody ProductRequest productRequest){
      return bookService.addProductById(id,productRequest);
    }
    @PostMapping("/out-product")
    @Operation(summary = "sell book")
    private ResponseEntity<?> outProduct(@Param("book id") Integer id, @RequestBody CustomerRequest customerRequest){
        return  bookService.outProductById(id,customerRequest);
    }

    @GetMapping("/get-book-of-the-week")
    @Operation(summary = "get book to table book of the week")
    public ResponseEntity<?> getBookOfTheWeek(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        return bookService.getBookOfTheWeek(page,size);
    }
    @GetMapping("/get-best-sell")
    @Operation(summary = "get book to table Best sell")
    public ResponseEntity<?> getBestSell(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        return bookService.getBestSell(page,size);
    }
    @GetMapping("/get-new-arrival")
    @Operation(summary = "get book to table New arrival")
    public ResponseEntity<?> getNewArrival(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size){
        return bookService.getNewArrival(page,size);
    }
    @PostMapping("/add-book-of-the-week")
    @Operation(summary = "add book to table book of the week")
    public ResponseEntity<?> addBookOfTheWeek(@RequestBody RequestById bookIds){
        return bookService.addBookOfTheWeek(bookIds);
    }
    @PostMapping("/add-best-sell")
    @Operation(summary = "add book to table Best sell")
    public ResponseEntity<?> AddBestSell(@RequestBody RequestById bookIds){
        return bookService.addBestSell(bookIds);
    }
    @PostMapping("/add-new-arrival")
    @Operation(summary = "add book to table New arrival")
    public ResponseEntity<?> addNewArrival(@RequestBody RequestById bookIds){
        return bookService.addNewArrival(bookIds);
    }
    @PostMapping("/delete-book-of-the-week")
    @Operation(summary = "add book to table book of the week")
    public ResponseEntity<?> deleteBookOfTheWeek(@RequestBody RequestById bookIds){
        return bookService.deleteBookOfTheWeek(bookIds);
    }
    @PostMapping("/delete-best-sell")
    @Operation(summary = "add book to table Best sell")
    public ResponseEntity<?> deleteBestSell(@RequestBody RequestById bookIds){
        return bookService.deleteBestSell(bookIds);
    }
    @PostMapping("/delete-new-arrival")
    @Operation(summary = "add book to table New arrival")
    public ResponseEntity<?> deleteNewArrival(@RequestBody RequestById bookIds){
        return bookService.deleteNewArrival(bookIds);
    }
}
