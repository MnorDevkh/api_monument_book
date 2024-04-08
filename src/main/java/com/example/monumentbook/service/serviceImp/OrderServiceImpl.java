package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.Cart;
import com.example.monumentbook.model.Order;
import com.example.monumentbook.model.User;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.UserDto;
import com.example.monumentbook.model.requests.OrderRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.OrderResponse;
import com.example.monumentbook.repository.BookRepository;
import com.example.monumentbook.repository.OrderRepository;
import com.example.monumentbook.repository.UserRepository;
import com.example.monumentbook.service.OrderService;
import com.example.monumentbook.utilities.response.ResponseObject;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<?> allCustomerOrder(Integer page, Integer size) {
//        ResponseObject res = new ResponseObject(); // Move inside the try block
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<Order> pageResult = orderRepository.findAll(pageable);
            List<OrderResponse> orderList = new ArrayList<>();
            for(Order order : pageResult){
                Optional<User> user = userRepository.findById((int) order.getUserId().getId());
                UserDto userDto = user.map(this::buildUserDto).orElse(null);
                Optional<Book> book = bookRepository.findById(order.getBookId().getId());
                BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                Optional<Order> customerOrderOptional = orderRepository.findById(order.getId());
                if (customerOrderOptional.isPresent()){
                    OrderResponse orderResponse = OrderResponse.builder()
                            .id(customerOrderOptional.get().getId())
                            .book(bookDto)
                            .user(userDto)
                            .price(customerOrderOptional.get().getPrice())
                            .qty(customerOrderOptional.get().getQty())
                            .date(customerOrderOptional.get().getDate())
                            .build();
                    orderList.add(orderResponse);
                }
                ApiResponse res = new ApiResponse(true, "Fetch books successful!", orderList, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
                return ResponseEntity.ok(res);
            }

            return ResponseEntity.ok(orderList);
        }catch (Exception e){
            ApiResponse res = new ApiResponse(true, "Fetch books successful!", null, 0, 0, 0,0);
            return ResponseEntity.ok(res);
        }
    }

    @Override
    public ResponseEntity<?> allCurrentOrder(Integer page, Integer size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<Order> pageResult = orderRepository.findByUserIdIdAndDeletedFalse(currentUser.getId(), pageable);
            List<OrderResponse> orderResponseList = new ArrayList<>();
            for (Order order : pageResult) {
                Optional<User> user = userRepository.findById((int) order.getUserId().getId());
                UserDto userDto = user.map(this::buildUserDto).orElse(null);
                Optional<Book> book = bookRepository.findById(order.getBookId().getId());
                BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                Optional<Order> customerOrderOptional = orderRepository.findById(order.getId());
                if (customerOrderOptional.isPresent()) {
                    OrderResponse orderResponse = OrderResponse.builder()
                            .id(customerOrderOptional.get().getId())
                            .book(bookDto)
                            .user(userDto)
                            .price(customerOrderOptional.get().getPrice())
                            .qty(customerOrderOptional.get().getQty())
                            .date(customerOrderOptional.get().getDate())
                            .build();
                    orderResponseList.add(orderResponse);
                }
            }

            ApiResponse res = new ApiResponse(true, "Fetch books successful!", orderResponseList, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseObject res = new ResponseObject(); // Create a new object for error handling
            res.setMessage("fetch data failed");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> newOrder(List<OrderRequest> orderRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;
        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            // Handle the case when the principal is not an instance of User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        List<Object> responses = new ArrayList<>(); // List to store individual responses

        for (OrderRequest orderRequest : orderRequests) {
            ResponseEntity<?> response = processCustomerRequest(currentUser, orderRequest);
            responses.add(response.getBody()); // Add the response body to the list
        }
        return ResponseEntity.ok(responses); // Return the list of responses
    }
    private ResponseEntity<?> processCustomerRequest(User currentUser, OrderRequest orderRequest) {
        try {
            Integer id = orderRequest.getProductId();
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                Book book = bookOptional.get();
//                int requestedQty = orderRequest.getQty();
//                int availableQty = book.getQty();
//                if (requestedQty <= availableQty) {
//                    book.setQty(availableQty - requestedQty);
//                    bookRepository.save(book);
                    Order order = Order.builder()
                            .bookId(bookOptional.get())
                            .userId(currentUser)
                            .qty(orderRequest.getQty())
                            .price(book.getPrice())
                            .date(LocalDate.now())
                            .build();
                    orderRepository.save(order);
                    OrderResponse orderResponse = orderResponseFlags(order);
                    return ResponseEntity.ok(orderResponse);
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

//                } else {
//                    ResponseObject res = new ResponseObject();
//                    res.setMessage("Requested quantity exceeds available quantity");
//                    res.setStatus(false);
//                    res.setData(null);
//
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found or deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    private BookDto buildBookDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .title(book.getTitle())
                .coverImg(book.getCoverImg())
                .publishDate(book.getPublishDate())
                .isbn(book.getIsbn())
                .build();
    }

    private UserDto buildUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .coverImage(user.getCoverImg())
                .email(user.getEmail())
                .build();
    }
    private OrderResponse orderResponseFlags(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .book(order.getBookId().toDto())
                .date(order.getDate())
                .qty(order.getQty())
                .price(order.getPrice())
                .user(order.getUserId().toDto())
                .build();
    }


}
