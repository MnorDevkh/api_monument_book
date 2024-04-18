package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.*;
import com.example.monumentbook.model.dto.AuthorDto;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.CategoryDto;
import com.example.monumentbook.model.requests.BookRequest;
import com.example.monumentbook.model.requests.OrderItemRequest;
import com.example.monumentbook.model.requests.PurchaseRequest;
import com.example.monumentbook.model.requests.RequestById;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.BookResponse;
import com.example.monumentbook.model.responses.PurchaseResponse;
import com.example.monumentbook.repository.*;
import com.example.monumentbook.service.BookService;
import com.example.monumentbook.utilities.response.ResponseObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImp implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorBookRepository authorBookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;




    @Override
    public ResponseEntity<?> findAllBook(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeletedFalse(pageable).map(Book::toDto);
            List<BookResponse> bookObj = new ArrayList<>();
            for (BookDto bookDto : pageResult.getContent()) {
                Optional<Book> bookOptional = bookRepository.findById(bookDto.getId());
                if (bookOptional.isPresent()) {
                    List<CategoryDto> categoryObj = new ArrayList<>();
                    List<BookCategory> categoryByBook = bookCategoryRepository.findBookCategoryByBook(bookOptional.get());
                    for (BookCategory bookCategory : categoryByBook) {
                        Optional<Category> categoryOptional = categoryRepository.findById(bookCategory.getCategory().getId());
                        if (categoryOptional.isPresent()) {
                            CategoryDto categoryDto = CategoryDto.builder()
                                    .id(categoryOptional.get().getId())
                                    .name(categoryOptional.get().getName())
                                    .description(categoryOptional.get().getDescription())
                                    .build();
                            categoryObj.add(categoryDto);
                        }
                    }
                    List<AuthorDto> authorObj = new ArrayList<>();
                    List<AuthorBook> categoryByAuthor = authorBookRepository.findAllByBook(bookOptional.get());
                    for (AuthorBook authorBook : categoryByAuthor) {
                        Optional<Author> authorOptional = authorRepository.findById(authorBook.getAuthors().getId());
                        if (authorOptional.isPresent()) {
                            AuthorDto authorDto = AuthorDto.builder()
                                    .id(authorOptional.get().getId())
                                    .name(authorOptional.get().getName())
                                    .description(authorOptional.get().getDescription())
                                    .image(authorOptional.get().getImage())
                                    .build();
                            authorObj.add(authorDto);
                        }
                    }
                    BookResponse bookResponse = BookResponse.builder()
                            .id(bookOptional.get().getId())
                            .title(bookOptional.get().getTitle())
                            .description(bookOptional.get().getDescription())
                            .isbn(bookOptional.get().getIsbn())
                            .coverImg(bookOptional.get().getCoverImg())
                            .publisher(bookOptional.get().getPublisher())
                            .publishDate(bookOptional.get().getPublishDate())
                            .price(bookOptional.get().getPrice())
                            .qty(bookOptional.get().getQty())
                            .author(authorObj)
                            .categories(categoryObj)
                            .build();
                    bookObj.add(bookResponse);
                }
            }

            if (!bookObj.isEmpty()) {
                ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookObj, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.ok(res);
            } else {
                ApiResponse res = new ApiResponse(false, "No books found!", null, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @Override
    public ResponseEntity<?> findBookById(Integer id) {
        try {
            List<BookResponse> bookObj = new ArrayList<>();
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent()) {
                List<CategoryDto> categoryObj = new ArrayList<>();
                List<BookCategory> categoryByBook = bookCategoryRepository.findBookCategoryByBook(bookOptional.get());
                for (BookCategory bookCategory : categoryByBook) {

                    Optional<Category> categoryOptional = categoryRepository.findById(bookCategory.getCategory().getId());
                    if (categoryOptional.isPresent()) {
                        CategoryDto categoryDto = CategoryDto.builder()
                                .id(categoryOptional.get().getId())
                                .name(categoryOptional.get().getName())
                                .description(categoryOptional.get().getDescription())
                                .build();
                        categoryObj.add(categoryDto);
                    }

                }
                List<AuthorDto> authorObj = new ArrayList<>();
                List<AuthorBook> categoryByAuthor = authorBookRepository.findAllByBook(bookOptional.get());
                for (AuthorBook authorBook : categoryByAuthor) {
                    Optional<Author> authorOptional = authorRepository.findById(authorBook.getAuthors().getId());
                    if (authorOptional.isPresent()) {
                        AuthorDto authorDto = AuthorDto.builder()
                                .id(authorOptional.get().getId())
                                .name(authorOptional.get().getName())
                                .description(authorOptional.get().getDescription())
                                .image(authorOptional.get().getImage())
                                .build();
                        authorObj.add(authorDto);
                    }

                }
                BookResponse bookResponse = BookResponse.builder()
                        .id(bookOptional.get().getId())
                        .title(bookOptional.get().getTitle())
                        .description(bookOptional.get().getDescription())
                        .isbn(bookOptional.get().getIsbn())
                        .coverImg(bookOptional.get().getCoverImg())
                        .publisher(bookOptional.get().getPublisher())
                        .publishDate(bookOptional.get().getPublishDate())
                        .price(bookOptional.get().getPrice())
                        .qty(bookOptional.get().getQty())
                        .author(authorObj)
                        .categories(categoryObj)
                        .build();
                bookObj.add(bookResponse);
                ResponseObject res = new ResponseObject();
                res.setStatus(true);
                res.setMessage("fetch book successful!");
                res.setData(bookObj);
                return ResponseEntity.ok(res);
            } else {
                ResponseObject res = new ResponseObject();
                res.setStatus(false);
                res.setMessage("Book not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @Override
    @Transactional
    public ResponseEntity<?> saveBook(BookRequest bookRequest) {
        try {
            Book book = Book.builder()
                    .isbn(bookRequest.getIsbn())
                    .title(bookRequest.getTitle())
                    .description(bookRequest.getDescription())
                    .coverImg(bookRequest.getCoverImg())
                    .publisher(bookRequest.getPublisher())
                    .publishDate(bookRequest.getPublishDate())
                    .price(bookRequest.getPrice())
                    .build();
            bookRepository.save(book);
            List<AuthorBook> authorBookList = new ArrayList<>();
            for (Integer author : bookRequest.getAuthorId()) {
                Optional<Author> authorOptional = authorRepository.findById(author);
                if (authorOptional.isPresent()) {
                    AuthorBook authorBook = AuthorBook.builder()
                            .authors(authorOptional.get())
                            .book(book)
                            .build();
                    authorBookList.add(authorBook);
                }
            }
            List<BookCategory> bookCategoryList = new ArrayList<>();
            for (Integer category : bookRequest.getCategoryId()) {
                Optional<Category> categoryOptional = categoryRepository.findById(category);
                if (categoryOptional.isPresent()) {
                    BookCategory bookCategory = BookCategory.builder()
                            .book(book)
                            .category(categoryOptional.get())
                            .build();
                    bookCategoryList.add(bookCategory);
                }
            }
            bookCategoryRepository.saveAll(bookCategoryList);
            authorBookRepository.saveAll(authorBookList);
            List<CategoryDto> categories = new ArrayList<>();
            for (Integer category : bookRequest.getCategoryId()) {
                Optional<Category> categoryOptional = categoryRepository.findById(category);
                if (categoryOptional.isPresent()) {
                    CategoryDto categoryDto = CategoryDto.builder()
                            .id(category)
                            .description(categoryOptional.get().getDescription())
                            .name(categoryOptional.get().getName())
                            .build();
                    categories.add(categoryDto);
                }
            }
            List<AuthorDto> authorDtos = new ArrayList<>();
            for (Integer author : bookRequest.getAuthorId()) {
                Optional<Author> authorOptional = authorRepository.findById(author);
                if (authorOptional.isPresent()) {
                    AuthorDto authorDto = AuthorDto.builder()
                            .id(authorOptional.get().getId())
                            .name(authorOptional.get().getName())
                            .description(authorOptional.get().getDescription())
                            .image(authorOptional.get().getImage())
                            .build();
                    authorDtos.add(authorDto);
                }
            }
            ResponseObject res = new ResponseObject();
            BookResponse bookResponse = BookResponse.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .description(book.getDescription())
                    .isbn(book.getIsbn())
                    .coverImg(book.getCoverImg())
                    .publisher(book.getPublisher())
                    .publishDate(book.getPublishDate())
                    .author(authorDtos)
                    .categories(categories)
                    .build();
            res.setMessage("book add success!");
            res.setData(bookResponse);
            res.setStatus(true);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<?> updateBook(BookRequest bookRequest, Integer id) {
        try {
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent()) {
                Book book = Book.builder()
                        .id(id)
                        .isbn(bookRequest.getIsbn())
                        .title(bookRequest.getTitle())
                        .description(bookRequest.getDescription())
                        .coverImg(bookRequest.getCoverImg())
                        .publisher(bookRequest.getPublisher())
                        .publishDate(bookRequest.getPublishDate())
                        .price(bookRequest.getPrice())
                        .qty(bookOptional.get().getQty())
                        .deleted(bookOptional.get().isDeleted())
                        .build();
                bookRepository.save(book);
                List<AuthorBook> authorBookList = new ArrayList<>();
                for (Integer author : bookRequest.getAuthorId()) {
                    Optional<Author> authorOptional = authorRepository.findById(author);
                    List<AuthorBook> authorBooks = authorBookRepository.findAllByBook(book);
                    for (AuthorBook authorBook : authorBooks) {
                        authorBookRepository.deleteById(authorBook.getId());
                    }
                    if (authorOptional.isPresent()) {
                        AuthorBook authorBook = AuthorBook.builder()
                                .authors(authorOptional.get())
                                .book(book)
                                .build();
                        authorBookList.add(authorBook);

                    }
                    authorBookRepository.deleteById(author);
                }
                List<BookCategory> bookCategoryList = new ArrayList<>();
                for (Integer category : bookRequest.getCategoryId()) {
                    Optional<Category> categoryOptional = categoryRepository.findById(category);
                    List<BookCategory> bookCategoryList1 = bookCategoryRepository.findBookCategoryByBook(book);
                    for (BookCategory bookCategory : bookCategoryList1) {
                        bookCategoryRepository.deleteById(bookCategory.getId());
                    }
                    if (categoryOptional.isPresent()) {
                        BookCategory bookCategory = BookCategory.builder()
                                .book(book)
                                .category(categoryOptional.get())
                                .build();
                        bookCategoryList.add(bookCategory);

                    }

                }
                bookCategoryRepository.saveAll(bookCategoryList);
                authorBookRepository.saveAll(authorBookList);
                List<CategoryDto> categories = new ArrayList<>();
                for (Integer category : bookRequest.getCategoryId()) {
                    Optional<Category> categoryOptional = categoryRepository.findById(category);
                    if (categoryOptional.isPresent()) {
                        CategoryDto categoryDto = CategoryDto.builder()
                                .id(category)
                                .description(categoryOptional.get().getDescription())
                                .name(categoryOptional.get().getName())
                                .build();
                        categories.add(categoryDto);
                    }
                }
                List<AuthorDto> authorDtos = new ArrayList<>();
                for (Integer author : bookRequest.getAuthorId()) {
                    Optional<Author> authorOptional = authorRepository.findById(author);
                    if (authorOptional.isPresent()) {
                        AuthorDto authorDto = AuthorDto.builder()
                                .id(authorOptional.get().getId())
                                .name(authorOptional.get().getName())
                                .description(authorOptional.get().getDescription())
                                .image(authorOptional.get().getImage())
                                .build();
                        authorDtos.add(authorDto);
                    }
                }
                ResponseObject res = new ResponseObject();
                BookResponse bookResponse = BookResponse.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .description(book.getDescription())
                        .isbn(book.getIsbn())
                        .coverImg(book.getCoverImg())
                        .publisher(book.getPublisher())
                        .publishDate(book.getPublishDate())
                        .author(authorDtos)
                        .categories(categories)
                        .qty(book.getQty())
                        .build();
                res.setStatus(true);
                res.setMessage("update success!" + " id" + id);
                res.setData(bookResponse);
                return ResponseEntity.ok(res);
            } else {
                ResponseObject res = new ResponseObject();
                res.setStatus(false);
                res.setMessage("update false!" + " id" + id);
                res.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @Override
    public ResponseEntity<?> DeleteById(Integer id) {
        try {
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                Book book = Book.builder()
                        .id(id)
                        .isbn(bookOptional.get().getIsbn())
                        .title(bookOptional.get().getTitle())
                        .description(bookOptional.get().getDescription())
                        .coverImg(bookOptional.get().getCoverImg())
                        .publisher(bookOptional.get().getPublisher())
                        .publishDate(bookOptional.get().getPublishDate())
                        .deleted(true)
                        .build();
                bookRepository.save(book);
                ResponseObject res = new ResponseObject();
                res.setStatus(true);
                res.setMessage("delete success!" + " id" + id);
                res.setData(book);
                return ResponseEntity.ok(res);

            } else {
                ResponseObject res = new ResponseObject();
                res.setStatus(false);
                res.setMessage("delete false!" + " id" + id);
                res.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @Override
    @Transactional
    public ResponseEntity<?> purchaseById(PurchaseRequest purchaseRequest) {

        try {
            Optional<Book> bookOptional = bookRepository.findById(purchaseRequest.getBook());
            ResponseObject res = new ResponseObject();

            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                Book book = Book.builder()
                        .id(bookOptional.get().getId())
                        .isbn(bookOptional.get().getIsbn())
                        .title(bookOptional.get().getTitle())
                        .description(bookOptional.get().getDescription())
                        .coverImg(bookOptional.get().getCoverImg())
                        .publisher(bookOptional.get().getPublisher())
                        .publishDate(bookOptional.get().getPublishDate())
                        .price(bookOptional.get().getPrice())
                        .qty(bookOptional.get().getQty() + purchaseRequest.getQty())
                        .deleted(bookOptional.get().isDeleted())
                        .bestSell(bookOptional.get().isBestSell())
                        .newArrival(bookOptional.get().isNewArrival())
                        .ofTheWeek(bookOptional.get().isOfTheWeek())
                        .build();
                bookRepository.save(book);
                Optional<Supplier> supplierOptional = supplierRepository.findById(purchaseRequest.getSupplier());

                if (supplierOptional.isPresent()) {
                    Purchase purchase = Purchase.builder()
                            .tax(purchaseRequest.getTax())
                            .invoice(purchaseRequest.getInvoice())
                            .cost(purchaseRequest.getCost())
                            .qty(purchaseRequest.getQty())
                            .book(bookOptional.get())
                            .date(LocalDate.now())
                            .supplier(supplierOptional.get())
                            .build();
                    productRepository.save(purchase);

                    res.setStatus(true);
                    res.setMessage("add success!");
                    res.setData(purchase);
                }
                return ResponseEntity.ok(res);
            } else {
                res.setStatus(false);
                res.setMessage("add false!");
                res.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> processCheckoutById(List<OrderItemRequest> orderItemRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;
        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            // Handle the case when the principal is not an instance of User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        List<Object> responses = new ArrayList<>(); // List to store individual responses

        for (OrderItemRequest orderItemRequest : orderItemRequests) {
            ResponseEntity<?> response = processCustomerRequest(currentUser, orderItemRequest);
            responses.add(response.getBody()); // Add the response body to the list
        }
        return ResponseEntity.ok(responses); // Return the list of responses
    }

    private ResponseEntity<?> processCustomerRequest(User currentUser, OrderItemRequest orderItemRequest) {
//        try {
//            Integer id = orderItemRequest.getProductId();
//            Optional<Book> bookOptional = bookRepository.findById(id);
//            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
//                Book book = bookOptional.get();
//                int requestedQty = orderItemRequest.getQty();
//                int availableQty = book.getQty();
//                if (requestedQty <= availableQty) {
//                    book.setQty(availableQty - requestedQty);
//                    bookRepository.save(book);
//                    OrderItem order = OrderItem.builder()
//                            .bookId(bookOptional.get())
//                            .userId(currentUser)
//                            .qty(requestedQty)
//                            .price(book.getPrice())
//                            .date(LocalDate.now())
//                            .build();
//                    orderRepository.save(order);
//                    try {
//                        ResponseObject res = new ResponseObject();
//                        Optional<Cart> cartOptional = cartRepository.findById(id);
//                        if (cartOptional.isPresent()) {
//                            Cart cart = cartOptional.get();
//                            cartRepository.delete(cart);
//                            res.setMessage("Deleted cart with id " + id);
//                            res.setStatus(true);
//                            res.setData(null);
//                            return ResponseEntity.ok(res);
//                        } else {
//                            res.setMessage("Cart with id " + id + " not found");
//                            res.setStatus(false);
//                            res.setData(null);
//                            return ResponseEntity.notFound().build();
//                        }
//                    } catch (Exception e) {
//                        ResponseObject res = new ResponseObject();
//                        res.setMessage("Failed to delete cart with id " + id);
//                        res.setStatus(false);
//                        res.setData(e);
//                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
//                    }
//
//                } else {
//                    ResponseObject res = new ResponseObject();
//                    res.setMessage("Requested quantity exceeds available quantity");
//                    res.setStatus(false);
//                    res.setData(null);
//
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found or deleted");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
        return null;
    }


    //get book of the week
    @Override
    public ResponseEntity<?> getBookOfTheWeek(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeletedFalseAndOfTheWeekTrue(pageable).map(Book::toDto);
            List<BookResponse> bookObj = new ArrayList<>();
            for (BookDto book : pageResult.getContent()) {
                Optional<Book> bookOptional = bookRepository.findById(book.getId());
                if (bookOptional.isPresent()) {
                    List<CategoryDto> categoryObj = getCategoriesByBookId(bookOptional.get());
                    List<AuthorDto> authorObj = getAuthorsByBookId(bookOptional.get());
                    BookResponse bookResponse = createBookResponse(bookOptional.get(), categoryObj, authorObj);
                    bookObj.add(bookResponse);
                }
            }
            if (!bookObj.isEmpty()) {
                ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookObj, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), bookObj.size());
                return ResponseEntity.ok(res);
            } else {
                ApiResponse res = new ApiResponse(false, "No books found!", null, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @Override
    public ResponseEntity<?> getBestSell(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeletedFalseAndBestSellTrue(pageable).map(Book::toDto);
            List<BookResponse> bookObj = new ArrayList<>();
            for (BookDto book : pageResult.getContent()) {
                Optional<Book> bookOptional = bookRepository.findById(book.getId());
                if (bookOptional.isPresent()) {
                    List<CategoryDto> categoryObj = getCategoriesByBookId(bookOptional.get());
                    List<AuthorDto> authorObj = getAuthorsByBookId(bookOptional.get());
                    BookResponse bookResponse = createBookResponse(bookOptional.get(), categoryObj, authorObj);
                    bookObj.add(bookResponse);
                }
            }
            if (!bookObj.isEmpty()) {
                ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookObj, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), bookObj.size());
                return ResponseEntity.ok(res);
            } else {
                ApiResponse res = new ApiResponse(false, "No books found!", null, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getNewArrival(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeletedFalseAndNewArrivalTrue(pageable).map(Book::toDto);
            List<BookResponse> bookObj = new ArrayList<>();
            for (BookDto book : pageResult.getContent()) {
                Optional<Book> bookOptional = bookRepository.findById(book.getId());
                if (bookOptional.isPresent()) {
                    List<CategoryDto> categoryObj = getCategoriesByBookId(bookOptional.get());
                    List<AuthorDto> authorObj = getAuthorsByBookId(bookOptional.get());

                    BookResponse bookResponse = createBookResponse(bookOptional.get(), categoryObj, authorObj);
                    bookObj.add(bookResponse);
                }
            }
            if (!bookObj.isEmpty()) {
                ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookObj, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), bookObj.size());
                return ResponseEntity.ok(res);
            } else {
                ApiResponse res = new ApiResponse(false, "No books found!", null, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
//    fuction update and update

    public ResponseEntity<?> BookFlags(RequestById requestById, String fieldName, boolean status) {
        try {
            List<Book> updatedBooks = new ArrayList<>();
            for (Integer id : requestById.getIdList()) {
                Optional<Book> bookOptional = bookRepository.findById(id);
                if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                    Book updatedBook = updateField(bookOptional.get(), fieldName, status);
                    updatedBooks.add(updatedBook);
                }
            }

            if (!updatedBooks.isEmpty()) {
                bookRepository.saveAll(updatedBooks);
                ApiResponse res = new ApiResponse(true, "Update books success!", updatedBooks, 0, 0, 0, 0);
                return ResponseEntity.ok(res);
            } else {
                ApiResponse res = new ApiResponse(false, "No valid books found to update.", null, 0, 0, 0, 0);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Book updateField(Book book, String fieldName, boolean status) {
        try {
            Field field = Book.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(book, status);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // Handle exception as needed
        }
        return book;
    }
    @Override
    public ResponseEntity<?> deleteBookOfTheWeek(RequestById requestById) {
        return BookFlags(requestById, "ofTheWeek",false);
    }

    @Override
    public ResponseEntity<?> deleteBestSell(RequestById requestById) {
        return BookFlags(requestById, "bestSell", false);
    }

    @Override
    public ResponseEntity<?> deleteNewArrival(RequestById requestById) {
        return BookFlags(requestById, "newArrival", false);
    }

    @Override
    public ResponseEntity<?> getAllPurchase() {
        try {
            List<Purchase> purchases = productRepository.findAll();
            if (purchases.isEmpty()) {
                ResponseObject res = new ResponseObject();
                res.setMessage("No purchases found");
                res.setStatus(false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            List<PurchaseResponse> purchaseRespons = new ArrayList<>();
            for (Purchase purchase : purchases) {
                PurchaseResponse purchaseResponse = createProductResponse(purchase);
                purchaseRespons.add(purchaseResponse);
            }

            ResponseObject res = new ResponseObject();
            res.setMessage("All purchases found");
            res.setStatus(true);
            res.setData(purchaseRespons);
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
            Optional<Purchase> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Optional<Book> bookOptional = bookRepository.findById(productOptional.get().getBook().getId());
                if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                    Book book = bookOptional.get();
                    book.setQty(book.getQty() - productOptional.get().getQty());
                    bookRepository.save(book);
                }
                productRepository.delete(productOptional.get());
                ResponseObject res = new ResponseObject();
                res.setMessage("Purchase deleted successfully");
                res.setStatus(true);
                res.setData(null);
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Purchase with ID: " + id + " not found");
            }
        } catch (Exception e) {
            ResponseObject res = new ResponseObject();
            res.setMessage("An error occurred while deleting the purchase");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> updatePurchase(Integer id, PurchaseRequest purchaseRequest) {
        try {
            Optional<Purchase> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Optional<Book> bookOptional = bookRepository.findById(purchaseRequest.getBook());
                if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                    Optional<Supplier> supplierOptional = supplierRepository.findById(purchaseRequest.getSupplier());
                    if (supplierOptional.isPresent()) {
                        Purchase purchase = productOptional.get();
                        purchase.setTax(purchaseRequest.getTax());
                        purchase.setInvoice(purchaseRequest.getInvoice());
                        purchase.setCost(purchaseRequest.getCost());
                        purchase.setQty(purchaseRequest.getQty());
                        purchase.setDate(LocalDate.now());
                        purchase.setSupplier(supplierOptional.get());
                        purchase.setBook(bookOptional.get());
                        Book book = bookOptional.get();
                        book.setQty(book.getQty() + purchaseRequest.getQty() - purchase.getQty());
                        bookRepository.save(book);
                        productRepository.save(purchase);

                        PurchaseResponse purchaseResponse = createProductResponse(purchase);
                        ResponseObject res = new ResponseObject();
                        res.setStatus(true);
                        res.setMessage("Update success!");
                        res.setData(purchaseResponse);
                        return ResponseEntity.ok(res);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Supplier with ID: " + purchaseRequest.getSupplier() + " not found");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Book with ID: " + purchaseRequest.getBook() + " not found or deleted");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase with ID: " + id + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getPurchaseById(Integer id) {
        try {
            ResponseObject res = new  ResponseObject();
            Optional<Purchase> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()){
                PurchaseResponse purchaseResponse = createProductResponse(productOptional.get());
                res.setMessage("fetch all purchase found");
                res.setStatus(true);
                res.setData(purchaseResponse);
            }
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            ResponseObject res = new  ResponseObject();
            res.setMessage("fetch all purchase found");
            res.setStatus(true);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }


    @Override
    public ResponseEntity<?> addBookOfTheWeek(RequestById requestById) {
        return BookFlags(requestById, "ofTheWeek", true);
    }

    @Override
    public ResponseEntity<?> addBestSell(RequestById requestById) {
        return BookFlags(requestById, "bestSell", true);
    }

    @Override
    public ResponseEntity<?> addNewArrival(RequestById requestById) {
        return BookFlags(requestById, "newArrival",  true);
    }


    //    fun getCategory
    private List<CategoryDto> getCategoriesByBookId(Book bookId) {
        List<CategoryDto> categoryObj = new ArrayList<>();
        List<BookCategory> categoryByBook = bookCategoryRepository.findBookCategoryByBook(bookId);

        for (BookCategory bookCategory : categoryByBook) {
            Optional<Category> categoryOptional = categoryRepository.findById(bookCategory.getCategory().getId());
            categoryOptional.ifPresent(category -> {
                CategoryDto categoryDto = CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build();
                categoryObj.add(categoryDto);
            });
        }
        return categoryObj;
    }
//    fun get Author
    private List<AuthorDto> getAuthorsByBookId(Book bookId) {
        List<AuthorDto> authorObj = new ArrayList<>();
        List<AuthorBook> categoryByBook = authorBookRepository.findAllByBook(bookId);

        for (AuthorBook authorBook : categoryByBook) {
            Optional<Author> authorOptional = authorRepository.findById(authorBook.getAuthors().getId());
            authorOptional.ifPresent(author -> {
                AuthorDto authorDto = AuthorDto.builder()
                        .id(author.getId())
                        .name(author.getName())
                        .description(author.getDescription())
                        .image(author.getImage())
                        .build();
                authorObj.add(authorDto);
            });
        }

        return authorObj;
    }

    //fun response
    private BookResponse createBookResponse(Book book, List<CategoryDto> categoryObj, List<AuthorDto> authorObj) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .coverImg(book.getCoverImg())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .price(book.getPrice())
                .qty(book.getQty())
                .author(authorObj)
                .categories(categoryObj)
                .build();
    }
    private PurchaseResponse createProductResponse(Purchase purchase){
        return PurchaseResponse.builder()
                .supplier(purchase.getSupplier().toDto())
                .id(purchase.getId())
                .invoice(purchase.getInvoice())
                .tax(purchase.getTax())
                .cost(purchase.getCost())
                .qty(purchase.getQty())
                .date(purchase.getDate())
                .book(purchase.getBook().toDto())
                .build();
    }

}
