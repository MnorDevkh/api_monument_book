package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.enums.Action;
import com.example.monumentbook.model.*;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.OrderItemDto;
import com.example.monumentbook.model.dto.PaymentDto;
import com.example.monumentbook.model.dto.UserDto;
import com.example.monumentbook.model.requests.OrderItemRequest;
import com.example.monumentbook.model.requests.OrderRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.OrderItemResponse;
import com.example.monumentbook.model.responses.OrderResponse;
import com.example.monumentbook.repository.*;
import com.example.monumentbook.service.OrderService;
import com.example.monumentbook.utilities.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public ResponseEntity<?> allOrder(Integer page, Integer size) {
//        ResponseObject res = new ResponseObject(); // Move inside the try block
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<Order> pageResult = orderRepository.findAll(pageable);
            List<OrderResponse> orderResponse = orderResponseFlags(pageResult);
            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponse, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
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
    public ResponseEntity<?> findById(Integer id) {
       try {
           Optional<Order> orderOptional = orderRepository.findById(id);
           ResponseObject res = new  ResponseObject();
           if (orderOptional.isPresent()) {
               Optional<User> user = userRepository.findById((int) orderOptional.get().getUserId().getId());
               UserDto userDto = user.map(this::buildUserDto).orElse(null);
               Optional<Payment> payment = paymentRepository.findById(orderOptional.get().getPayment().getId());
               PaymentDto paymentDto = payment.map(this::buildPaymentDto).orElse(null);
               List<OrderItemDto> orderItemResponses = new ArrayList<>();
               List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderOptional.get().getId());
               System.out.println("orderItems" + orderItems);
               for (OrderItem orderItem : orderItems) {
                   Optional<Book> book = bookRepository.findById(orderItem.getBookId().getId());
                   BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                   OrderItemDto orderItemDto = OrderItemDto.builder()
                           .id(orderItem.getId())
                           .book(bookDto)
                           .price(orderItem.getPrice())
                           .qty(orderItem.getQty())
                           .build();
                   orderItemResponses.add(orderItemDto);
               }

               OrderResponse orderResponse = OrderResponse.builder()
                       .id(orderOptional.get().getId())
                       .type(orderOptional.get().isType())
                       .action(orderOptional.get().getAction())
                       .date(orderOptional.get().getDate())
                       .payment(paymentDto)
                       .orderItem(orderItemResponses)
                       .phone(orderOptional.get().getPhone())
                       .address(orderOptional.get().getAddress())
                       .price(orderOptional.get().getPrice())
                       .qty(orderOptional.get().getQty())
                       .user(userDto)
                       .build();
               res.setStatus(true);
               res.setMessage("responses success");
               res.setData(orderResponse);
           }
           return ResponseEntity.ok(res);
       }catch (Exception e) {
           ResponseObject res = new ResponseObject(); // Create a new object for error handling
           res.setMessage("fetch data failed");
           res.setStatus(false);
           res.setData(null);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

       }
    }

    @Override
    public ResponseEntity<?> allCurrentOrder(Integer page, Integer size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<Order> pageResult = orderRepository.findByUserIdIdAndDeletedFalse(currentUser.getId(), pageable);

            List<OrderResponse> orderResponse = orderResponseFlags(pageResult);
            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponse, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
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
    public ResponseEntity<?> newOrder(OrderRequest orderRequest) {
        ResponseObject res = new ResponseObject();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;
        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            // Handle the case when the principal is not an instance of User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        Optional<Payment> paymentOptional = paymentRepository.findById(orderRequest.getPaymentId());
        if (paymentOptional.isPresent()){
            Order orderObj = Order.builder()
                    .action(Action.pending)
                    .type(orderRequest.isType())
                    .qty(orderRequest.getQty())
                    .price(orderRequest.getPrice())
                    .userId(currentUser)
                    .payment(paymentOptional.get())
                    .phone(orderRequest.getPhone())
                    .date(LocalDate.now())
                    .address(orderRequest.getAddress())
                    .build();
            orderRepository.save(orderObj);
            Order order = Order.builder()
                    .id(orderObj.getId())
                    .build();
            List<OrderItemDto> orderItemResponses = new ArrayList<>(); // List to store individual responses

            for (OrderItemRequest orderItemRequest : orderRequest.getOrderItem()) {
                ResponseEntity<?> response = processCustomerRequest(currentUser, orderItemRequest, order);
                OrderItemDto orderItemResponse = (OrderItemDto) response.getBody();
                orderItemResponses.add(orderItemResponse);
            }
            Optional<User> user = userRepository.findById((int) orderObj.getUserId().getId());
            UserDto userDto = user.map(this::buildUserDto).orElse(null);
            Optional<Payment> payment = paymentRepository.findById(orderObj.getPayment().getId());
            PaymentDto paymentDto = payment.map(this::buildPaymentDto).orElse(null);
            OrderResponse orderResponse = OrderResponse.builder()
                    .id(order.getId())
                    .type(orderObj.isType())
                    .action(orderObj.getAction())
                    .date(orderObj.getDate())
                    .payment(paymentDto)
                    .phone(orderObj.getPhone())
                    .orderItem(orderItemResponses)
                    .address(orderObj.getAddress())
                    .user(userDto)
                    .build();
            res.setStatus(true);
            res.setMessage("add successful!");
            res.setData(orderResponse);

        }


        return ResponseEntity.ok(res); // Return the list of responses
    }

    @Override
    public ResponseEntity<?> confirm(Integer id) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(id);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setAction(Action.confirmed); // Update the action to "Conform"
                orderRepository.save(order); // Save the updated order back to the database

                // Update book quantities and handle cart deletion
                updateBookQuantitiesAndDeleteCart(order);

                // Return a success response
                return ResponseEntity.ok("Order with ID " + id + " confirmed successfully.");
            } else {
                // Handle the case when the order with the given ID does not exist
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during the process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to confirm order: " + e.getMessage());
        }
    }

    private void updateBookQuantitiesAndDeleteCart(Order order) {
        // Retrieve order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {
            Optional<Book> bookOptional = bookRepository.findById(orderItem.getBookId().getId());
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                int requestedQty = orderItem.getQty();
                int availableQty = book.getQty();
                if (requestedQty <= availableQty) {
                    // Update book quantity
                    book.setQty(availableQty - requestedQty);
                    bookRepository.save(book);
                    // Delete cart items (if needed)
                    // Implement cart deletion logic here if necessary
                } else {
                    // Handle the case when requested quantity exceeds available quantity
                    // You may throw an exception or handle it differently based on your requirements
                }
            } else {
                // Handle the case when the book associated with the order item is not found
                // You may throw an exception or handle it differently based on your requirements
            }
        }
    }

    @Override
    public ResponseEntity<?> reject(Integer id) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(id);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setAction(Action.rejected); // Update the action to "Reject"
                orderRepository.save(order); // Save the updated order back to the database
                // You can return a success response here if needed
                return ResponseEntity.ok("Order with ID " + id + " reject successfully.");
            } else {
                // Handle the case when the order with the given ID does not exist
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during the process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> findPending(Integer pageNumber, Integer pageSize, String sortBy, boolean ascending) {
        try {
            Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageable = PageRequest.of(pageNumber -1, pageSize, sortDirection, sortBy);
            Page<Order> pageResult = orderRepository.findAllByAction(Action.pending,pageable);

            List<OrderResponse> orderResponse = orderResponseFlags(pageResult);
            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponse, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println(e);
            ResponseObject res = new ResponseObject(); // Create a new object for error handling
            res.setMessage("fetch data failed");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> findReject(Integer pageNumber, Integer pageSize, String sortBy, boolean ascending) {
        try {
            Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageable = PageRequest.of(pageNumber - 1, pageSize, sortDirection, sortBy);
            Page<Order> pageResult = orderRepository.findAllByAction(Action.rejected, pageable);



            List<OrderResponse> orderResponse = orderResponseFlags(pageResult);
            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponse, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println(e);
            ResponseObject res = new ResponseObject(); // Create a new object for error handling
            res.setMessage("fetch data failed");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> findConfirm(Integer pageNumber, Integer pageSize, String sortBy, boolean ascending) {
        try {
            Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageable = PageRequest.of(pageNumber -1, pageSize, sortDirection, sortBy);

            Page<Order> pageResult = orderRepository.findAllByAction(Action.confirmed,pageable);

            List<OrderResponse> orderResponse = orderResponseFlags(pageResult);
            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponse, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println(e);
            ResponseObject res = new ResponseObject(); // Create a new object for error handling
            res.setMessage("fetch data failed");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    private ResponseEntity<?> processCustomerRequest(User currentUser, OrderItemRequest orderItemRequest, Order order) {
        try {
            Integer id = orderItemRequest.getProductId();
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                Book book = bookOptional.get();
                OrderItem orderItem = OrderItem.builder()
                        .bookId(bookOptional.get())
                        .qty(orderItemRequest.getQty())
                        .price(book.getPrice())
                        .date(LocalDate.now())
                        .order(order)
                        .build();
                orderItemRepository.save(orderItem);
                OrderItemDto orderItemResponse = orderItemResponseDroFlags(orderItem);
                return ResponseEntity.ok(orderItemResponse);
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
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .coverImage(user.getCoverImg())
                .email(user.getEmail())
                .address(user.getAddress())
                .build();
    }
    private PaymentDto buildPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .name(payment.getName())
                .type(payment.getType())
                .cvv(payment.getCvv())
                .number(payment.getNumber())
                .build();
    }

    private OrderItemDto orderItemResponseDroFlags(OrderItem orderItem){
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .book(orderItem.getBookId().toDto())
                .qty(orderItem.getQty())
                .price(orderItem.getPrice())
                .build();
    }

        private List<OrderResponse> orderResponseFlags(Page<Order> pageResult) {
            List<OrderResponse> orderResponseList = new ArrayList<>();

            for (Order order : pageResult) {
                Optional<User> user = userRepository.findById((int) order.getUserId().getId());
                UserDto userDto = user.map(this::buildUserDto).orElse(null);
                Optional<Payment> payment = paymentRepository.findById(order.getPayment().getId());
                PaymentDto paymentDto = payment.map(this::buildPaymentDto).orElse(null);
                List<OrderItemDto> orderItemResponses = new ArrayList<>();
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                System.out.println("orderItems" + orderItems);
                for (OrderItem orderItem : orderItems) {
                    Optional<Book> book = bookRepository.findById(orderItem.getBookId().getId());
                    BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                    OrderItemDto orderItemDto = OrderItemDto.builder()
                            .id(orderItem.getId())
                            .book(bookDto)
                            .price(orderItem.getPrice())
                            .qty(orderItem.getQty())
                            .build();
                    orderItemResponses.add(orderItemDto);
                }

                OrderResponse orderResponse = OrderResponse.builder()
                        .id(order.getId())
                        .type(order.isType())
                        .action(order.getAction())
                        .date(order.getDate())
                        .payment(paymentDto)
                        .orderItem(orderItemResponses)
                        .phone(order.getPhone())
                        .qty(order.getQty())
                        .price(order.getPrice())
                        .address(order.getAddress())
                        .user(userDto)
                        .build();
                orderResponseList.add(orderResponse);
            }

            return orderResponseList;
        }

//    private OrderItemDto orderItemDtoFlags(){
//        return OrderItemDto.builder()
//                .id()
//                .build();
//    }


}
