package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.*;
import com.example.monumentbook.model.dto.AuthorDto;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.CategoryDto;
import com.example.monumentbook.model.requests.BookRequest;
import com.example.monumentbook.model.requests.CustomerRequest;
import com.example.monumentbook.model.requests.ProductRequest;
import com.example.monumentbook.model.requests.RequestById;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.BookResponse;
import com.example.monumentbook.model.responses.VendorResponse;
import com.example.monumentbook.repository.*;
import com.example.monumentbook.service.BookService;
import com.example.monumentbook.utilities.response.ResponseObject;
import jakarta.transaction.Transactional;
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
public class BookServiceImp implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorBookRepository authorBookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository customerRepository;
    private final CartRepository cartRepository;

    ResponseObject res = new ResponseObject();

    public BookServiceImp(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository, AuthorBookRepository authorBookRepository, BookCategoryRepository bookCategoryRepository, VendorRepository vendorRepository, OrderRepository customerRepository, CartRepository cartRepository) {

        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.authorBookRepository = authorBookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.vendorRepository = vendorRepository;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public ResponseEntity<?> findAllBook(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeleteFalse(pageable).map(Book::toDto);

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
                res.setStatus(true);
                res.setMessage("fetch book successful!");
                res.setData(bookObj);
                return ResponseEntity.ok(res);
            } else {
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
                        .delete(bookOptional.get().isDelete())
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
            if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
                Book book = Book.builder()
                        .id(id)
                        .isbn(bookOptional.get().getIsbn())
                        .title(bookOptional.get().getTitle())
                        .description(bookOptional.get().getDescription())
                        .coverImg(bookOptional.get().getCoverImg())
                        .publisher(bookOptional.get().getPublisher())
                        .publishDate(bookOptional.get().getPublishDate())
                        .delete(true)
                        .build();
                bookRepository.save(book);
                res.setStatus(true);
                res.setMessage("delete success!" + " id" + id);
                res.setData(book);
                return ResponseEntity.ok(res);
            } else {
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
    public ResponseEntity<?> addProductById(Integer id, ProductRequest productRequest) {

        try {
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
                Book book = Book.builder()
                        .id(bookOptional.get().getId())
                        .isbn(bookOptional.get().getIsbn())
                        .title(bookOptional.get().getTitle())
                        .description(bookOptional.get().getDescription())
                        .coverImg(bookOptional.get().getCoverImg())
                        .publisher(bookOptional.get().getPublisher())
                        .publishDate(bookOptional.get().getPublishDate())
                        .price(bookOptional.get().getPrice())
                        .qty(bookOptional.get().getQty() + productRequest.getQty())
                        .delete(bookOptional.get().isDelete())
                        .bestSell(bookOptional.get().isBestSell())
                        .newArrival(bookOptional.get().isNewArrival())
                        .ofTheWeek(bookOptional.get().isOfTheWeek())
                        .build();
                bookRepository.save(book);
                Vendor vendor = Vendor.builder()
                        .price(productRequest.getCost())
                        .name(productRequest.getVendor())
                        .qty(productRequest.getQty())
                        .date(LocalDate.now())
                        .book_id(bookOptional.get().getId())
                        .build();
                vendorRepository.save(vendor);
                res.setStatus(true);
                res.setMessage("add success!");
                res.setData(vendor);
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
    public ResponseEntity<?> outProductById(List<CustomerRequest> customerRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;
        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            // Handle the case when the principal is not an instance of User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        List<Object> responses = new ArrayList<>(); // List to store individual responses

        for (CustomerRequest customerRequest : customerRequests) {
            ResponseEntity<?> response = processCustomerRequest(currentUser, customerRequest);
            responses.add(response.getBody()); // Add the response body to the list
        }
        return ResponseEntity.ok(responses); // Return the list of responses
    }

    private ResponseEntity<?> processCustomerRequest(User currentUser, CustomerRequest customerRequest) {
        try {
            Integer id = customerRequest.getProductId();
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
                Book book = bookOptional.get();
                int requestedQty = customerRequest.getQty();
                int availableQty = book.getQty();
                if (requestedQty <= availableQty) {
                    book.setQty(availableQty - requestedQty);
                    bookRepository.save(book);
                    CustomerOrder customerOrder = CustomerOrder.builder()
                            .bookId(bookOptional.get())
                            .userId(currentUser)
                            .qty(requestedQty)
                            .price(book.getPrice())
                            .date(LocalDate.now())
                            .build();
                    customerRepository.save(customerOrder);
                    try {
                        Optional<Cart> cartOptional = cartRepository.findById(id);
                        if (cartOptional.isPresent()) {
                            Cart cart = cartOptional.get();
                            cartRepository.delete(cart);
                            res.setMessage("Deleted cart with id " + id);
                            res.setStatus(true);
                            res.setData(null);
                            return ResponseEntity.ok(res);
                        } else {
                            res.setMessage("Cart with id " + id + " not found");
                            res.setStatus(false);
                            res.setData(null);
                            return ResponseEntity.notFound().build();
                        }
                    } catch (Exception e) {
                        res.setMessage("Failed to delete cart with id " + id);
                        res.setStatus(false);
                        res.setData(e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                    }

                } else {
                    res.setMessage("Requested quantity exceeds available quantity");
                    res.setStatus(false);
                    res.setData(null);

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found or deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    //get book of the week
    @Override
    public ResponseEntity<?> getBookOfTheWeek(Integer page, Integer size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<BookDto> pageResult = bookRepository.findByDeleteFalseAndOfTheWeekTrue(pageable).map(Book::toDto);
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
            Page<BookDto> pageResult = bookRepository.findByDeleteFalseAndBestSellTrue(pageable).map(Book::toDto);
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
            Page<BookDto> pageResult = bookRepository.findByDeleteFalseAndNewArrivalTrue(pageable).map(Book::toDto);
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
                if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
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
    public ResponseEntity<?> getAllImportProduct(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Vendor> pageResult = vendorRepository.findByDeletedFalse(pageable);
        List<VendorResponse> vendorList = new ArrayList<>();
        for(Vendor vendor : pageResult){
            Optional<Vendor> vendorOptional = vendorRepository.findById(vendor.getId());
            if (vendorOptional.isPresent()){
                Optional<Book> bookOptional = bookRepository.findById(vendorOptional.get().getBook_id());
                if (bookOptional.isPresent()){
                    BookDto bookDto = BookDto.builder()
                            .id(bookOptional.get().getId())
                            .qty(bookOptional.get().getQty())
                            .description(bookOptional.get().getDescription())
                            .price(bookOptional.get().getPrice())
                            .coverImg(bookOptional.get().getCoverImg())
                            .isbn(bookOptional.get().getIsbn())
                            .title(bookOptional.get().getTitle())
                            .build();
                    VendorResponse vendorResponse = VendorResponse.builder()
                            .id(vendorOptional.get().getId())
                            .price(vendorOptional.get().getPrice())
                            .qty(vendorOptional.get().getQty())
                            .name(vendorOptional.get().getName())
                            .date(vendorOptional.get().getDate())
                            .book(bookDto)
                            .build();
                    vendorList.add(vendorResponse);
                }


            }

        }
        ApiResponse res = new ApiResponse(true, "Fetch books successful!", vendorList, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getContent().size());
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<?> deleteImport(Integer id) {
        try {
            Optional<Vendor> vendor = vendorRepository.findByIdAndDeletedFalse(id);
            System.out.println(vendor);
            if (vendor.isPresent()) {
                Optional<Book> bookOptional = bookRepository.findById(vendor.get().getBook_id());
                if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
                    Book book = Book.builder()
                            .id(bookOptional.get().getId())
                            .isbn(bookOptional.get().getIsbn())
                            .title(bookOptional.get().getTitle())
                            .description(bookOptional.get().getDescription())
                            .coverImg(bookOptional.get().getCoverImg())
                            .publisher(bookOptional.get().getPublisher())
                            .publishDate(bookOptional.get().getPublishDate())
                            .price(bookOptional.get().getPrice())
                            .qty(bookOptional.get().getQty() - vendor.get().getQty())
                            .delete(bookOptional.get().isDelete())
                            .bestSell(bookOptional.get().isBestSell())
                            .newArrival(bookOptional.get().isNewArrival())
                            .ofTheWeek(bookOptional.get().isOfTheWeek())
                            .build();
                    bookRepository.save(book);
                    Vendor vendorObj = Vendor.builder()
                            .id(vendor.get().getId())
                            .price(vendor.get().getPrice())
                            .name(vendor.get().getName())
                            .qty(vendor.get().getQty())
                            .date(LocalDate.now())
                            .book_id(vendor.get().getBook_id())
                            .deleted(true)
                            .build();
                    vendorRepository.save(vendorObj);
                    res.setStatus(true);
                    res.setMessage("add success!");
                    res.setData(vendor);
                    return ResponseEntity.ok(res);
                } else {
                    res.setStatus(false);
                    res.setMessage("add false!");
                    res.setData(null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found");

    }

    @Override
    public ResponseEntity<?> updateImport(Integer id,Integer book_id, ProductRequest productRequest) {
        try {
            Optional<Vendor> vendor = vendorRepository.findByIdAndDeletedFalse(id);
            System.out.println(vendor);
            if (vendor.isPresent()) {
                Optional<Book> bookOptional = bookRepository.findById(book_id);
                if (bookOptional.isPresent() && !bookOptional.get().isDelete()) {
                    Book book = Book.builder()
                            .id(bookOptional.get().getId())
                            .isbn(bookOptional.get().getIsbn())
                            .title(bookOptional.get().getTitle())
                            .description(bookOptional.get().getDescription())
                            .coverImg(bookOptional.get().getCoverImg())
                            .publisher(bookOptional.get().getPublisher())
                            .publishDate(bookOptional.get().getPublishDate())
                            .price(bookOptional.get().getPrice())
                            .qty(bookOptional.get().getQty() - vendor.get().getQty() +productRequest.getQty())
                            .delete(bookOptional.get().isDelete())
                            .bestSell(bookOptional.get().isBestSell())
                            .newArrival(bookOptional.get().isNewArrival())
                            .ofTheWeek(bookOptional.get().isOfTheWeek())
                            .build();
                    bookRepository.save(book);
                    Vendor vendorObj = Vendor.builder()
                            .id(vendor.get().getId())
                            .price(productRequest.getCost())
                            .name(productRequest.getVendor())
                            .qty(productRequest.getQty())
                            .date(vendor.get().getDate())
                            .book_id(bookOptional.get().getId())
                            .build();
                    vendorRepository.save(vendorObj);
                    res.setStatus(true);
                    res.setMessage("add success!");
                    res.setData(vendor);
                    return ResponseEntity.ok(res);
                } else {
                    res.setStatus(false);
                    res.setMessage("add false!");
                    res.setData(null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found");
    }

    @Override
    public ResponseEntity<?> getImportProductById(Integer id) {
        try {
            Optional<Vendor> vendorOptional = vendorRepository.findById(id);
            if (vendorOptional.isPresent()){
                Optional<Book> bookOptional = bookRepository.findById(vendorOptional.get().getBook_id());
                if (bookOptional.isPresent()) {
                    BookDto bookDto = BookDto.builder()
                            .id(bookOptional.get().getId())
                            .qty(bookOptional.get().getQty())
                            .description(bookOptional.get().getDescription())
                            .price(bookOptional.get().getPrice())
                            .coverImg(bookOptional.get().getCoverImg())
                            .isbn(bookOptional.get().getIsbn())
                            .title(bookOptional.get().getTitle())
                            .build();
                    VendorResponse vendorResponse = VendorResponse.builder()
                            .id(vendorOptional.get().getId())
                            .price(vendorOptional.get().getPrice())
                            .qty(vendorOptional.get().getQty())
                            .name(vendorOptional.get().getName())
                            .date(vendorOptional.get().getDate())
                            .book(bookDto)
                            .build();
                    res.setData(vendorResponse);
                    res.setStatus(true);
                    res.setMessage("get import by id successful!");
                }
            }
            return ResponseEntity.ok(res);
        }catch (Exception e){
            res.setData(null);
            res.setStatus(false);
            res.setMessage("get import by id false!");
            return ResponseEntity.ok(res);
        }
    }

//    @Override
//    public ResponseEntity<?> findBookBySearch(String keySearch) {
//        try {
////            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
////            Page<BookDto> pageResult = bookRepository.findByDeleteFalseAndNewArrivalTrue(pageable).map(Book::toDto);
//            List<Book> books = bookRepository.findByDeleteFalseAndTitleOrDescriptionOrIsbn(keySearch);
//            List<BookResponse> bookObj = new ArrayList<>();
//            for (Book book : books) {
//                Optional<Book> bookOptional = bookRepository.findById(book.getId());
//                if (bookOptional.isPresent()) {
//                    List<CategoryDto> categoryObj = getCategoriesByBookId(bookOptional.get());
//                    List<AuthorDto> authorObj = getAuthorsByBookId(bookOptional.get());
//
//                    BookResponse bookResponse = createBookResponse(bookOptional.get(), categoryObj, authorObj);
//                    bookObj.add(bookResponse);
//                }
//            }
//            if (!bookObj.isEmpty()) {
//                ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookObj, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), bookObj.size());
//                return ResponseEntity.ok(res);
//            } else {
//                ApiResponse res = new ApiResponse(false, "No books found!", null, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
//            }
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

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

}
