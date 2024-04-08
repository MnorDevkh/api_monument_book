package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.enums.Action;
import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.Order;
import com.example.monumentbook.model.OrderItem;
import com.example.monumentbook.model.User;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.OrderItemDto;
import com.example.monumentbook.model.dto.UserDto;
import com.example.monumentbook.model.requests.OrderItemRequest;
import com.example.monumentbook.model.requests.OrderRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.OrderItemResponse;
import com.example.monumentbook.model.responses.OrderResponse;
import com.example.monumentbook.repository.BookRepository;
import com.example.monumentbook.repository.OrderItemRepository;
import com.example.monumentbook.repository.OrderRepository;
import com.example.monumentbook.repository.UserRepository;
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

    @Override
    public ResponseEntity<?> allCustomerOrder(Integer page, Integer size) {
//        ResponseObject res = new ResponseObject(); // Move inside the try block
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<OrderItem> pageResult = orderItemRepository.findAll(pageable);
            List<OrderItemResponse> orderList = new ArrayList<>();
            for(OrderItem orderItem : pageResult){
//                Optional<User> user = userRepository.findById((int) orderItem.getUserId().getId());
//                UserDto userDto = user.map(this::buildUserDto).orElse(null);
                Optional<Book> book = bookRepository.findById(orderItem.getBookId().getId());
                BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                Optional<OrderItem> customerOrderOptional = orderItemRepository.findById(orderItem.getId());
                if (customerOrderOptional.isPresent()){
                    OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                            .id(customerOrderOptional.get().getId())
                            .book(bookDto)

                            .price(customerOrderOptional.get().getPrice())
                            .qty(customerOrderOptional.get().getQty())
                            .date(customerOrderOptional.get().getDate())
                            .build();
                    orderList.add(orderItemResponse);
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


            System.out.println(pageResult+ "pageResult");
            List<OrderResponse> orderResponseList = new ArrayList<>();

            for (Order order : pageResult) {
                Optional<User> user = userRepository.findById((int) order.getUserId().getId());
                UserDto userDto = user.map(this::buildUserDto).orElse(null);

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

                OrderResponse orderResponse = orderResponseFlags(order);
                orderResponse.setOrderItem(orderItemResponses);
                orderResponse.setUser(userDto);
                orderResponseList.add(orderResponse);
            }

            ApiResponse res = new ApiResponse(true, "Fetch orders successful!", orderResponseList, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;
        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            // Handle the case when the principal is not an instance of User
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        Order orderObj = Order.builder()
                .action(Action.Padding)
                .userId(currentUser)
                .date(LocalDate.now())
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
        // Construct OrderResponse using orderResponseFlags method
        OrderResponse orderResponse = orderResponseFlags(order);
        orderResponse.setOrderItem(orderItemResponses);

        return ResponseEntity.ok(orderResponse); // Return the list of responses
    }
    private ResponseEntity<?> processCustomerRequest(User currentUser, OrderItemRequest orderItemRequest, Order order) {
        try {
            Integer id = orderItemRequest.getProductId();
            Optional<Book> bookOptional = bookRepository.findById(id);
//            Optional<Order> orderOptional = orderRepository.findById(orderItemResponses.getId());
            if (bookOptional.isPresent() && !bookOptional.get().isDeleted()) {
                Book book = bookOptional.get();
//                int requestedQty = orderItemRequest.getQty();
//                int availableQty = book.getQty();
//                if (requestedQty <= availableQty) {
//                    book.setQty(availableQty - requestedQty);
//                    bookRepository.save(book);
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
    private OrderItemDto orderItemResponseDroFlags(OrderItem orderItem){
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .book(orderItem.getBookId().toDto())
//                .date(orderItem.getDate())
                .qty(orderItem.getQty())
                .price(orderItem.getPrice())

                .build();
    }
    private OrderResponse orderResponseFlags(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .type(order.isType())
                .action(order.getAction())
                .date(order.getDate())
                .build();
    }


}
