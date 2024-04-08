package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.Bookmarks;
import com.example.monumentbook.model.Cart;
import com.example.monumentbook.model.User;
import com.example.monumentbook.model.dto.BookDto;
import com.example.monumentbook.model.dto.UserDto;
import com.example.monumentbook.model.requests.BookmarksRequest;
import com.example.monumentbook.model.requests.CartRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.BookResponse;
import com.example.monumentbook.model.responses.BookmarksResponse;
import com.example.monumentbook.model.responses.CartResponse;
import com.example.monumentbook.repository.BookRepository;
import com.example.monumentbook.repository.BookmarkRepository;
import com.example.monumentbook.repository.UserRepository;
import com.example.monumentbook.service.BookmarksService;
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
public class BookmarksServiceImpl implements BookmarksService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    @Override
    public ResponseEntity<?> addToBookmarks(BookmarksRequest bookmarksRequest) {
        ResponseObject res = new ResponseObject();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Optional<Book> book = bookRepository.findById(bookmarksRequest.getBookId());

            if (book.isPresent()) {
                // Check if a cart entry already exists for the given userId and bookId
                Optional<Bookmarks> existingCart = bookmarkRepository.findByUserIdAndBookIdAndDeletedFalse(currentUser, book.get());
                if (existingCart.isPresent()) {
                    // Handle the conflict - cart entry already exists
                    res.setMessage("Cart entry already exists for the given user and book.");
                    res.setStatus(false);
                    res.setData(null);
                } else {
                    // Create and save the new cart entry
                    Bookmarks bookmarks = Bookmarks.builder()
                            .userId(currentUser)
                            .bookId(book.get())
                            .date(LocalDate.now())
                            .build();
                    bookmarkRepository.save(bookmarks);
                    // Build response objects
                    BookDto bookObj = buildBookDto(book.get());
                    UserDto userObj = buildUserDto(currentUser);
                    BookmarksResponse bookmarksResponse = BookmarksResponse.builder()
                            .id(bookmarks.getId())
                            .user(userObj)
                            .book(bookObj)
                            .date(bookmarks.getDate())
                            .build();

                    res.setMessage("Add successful!");
                    res.setStatus(true);
                    res.setData(bookmarksResponse);
                }
            } else {
                // Handle case where book or user is not found
                res.setMessage("Book or User not found.");
                res.setStatus(false);
                res.setData(null);
            }

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            // Handle any other exceptions
            res.setMessage("Error occurred while adding to cart.");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.ok(res);
        }
    }

    @Override
    public ResponseEntity<?> getBookmarks(Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteCartById(Integer id) {
        ResponseObject res= new ResponseObject();
        try {
            Optional<Bookmarks> bookmarksOptional = bookmarkRepository.findById(id);
            if(bookmarksOptional.isPresent()){
            Bookmarks bookmarks = Bookmarks.builder()
                    .id(id)
                    .userId(bookmarksOptional.get().getUserId())
                    .bookId(bookmarksOptional.get().getBookId())
                    .date(bookmarksOptional.get().getDate())
                    .deleted(true)
                    .build();
            bookmarkRepository.save(bookmarks);
            res.setMessage("delete successful cart id" + bookmarks.getId());
            res.setStatus(true);
            res.setData(null);
            }return ResponseEntity.ok(res);
        }catch (Exception e){
            res.setMessage("add false!");
            res.setStatus(true);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<?> findBookmarksByUser(Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        try{
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
            Page<Bookmarks> pageResult = bookmarkRepository.findByUserIdIdAndDeletedFalse(currentUser.getId(), pageable);

            List<BookmarksResponse> bookmarksResponses = new ArrayList<>();
            for (Bookmarks bookmarks : pageResult.getContent()) {
                Optional<User> user = userRepository.findById((int)bookmarks.getUserId().getId());
                UserDto userDto = user.map(this::buildUserDto).orElse(null);
                Optional<Book> book = bookRepository.findById(bookmarks.getBookId().getId());
                BookDto bookDto = book.map(this::buildBookDto).orElse(null);
                BookmarksResponse cartResponse = BookmarksResponse.builder()
                        .id(bookmarks.getId())
                        .user(userDto)
                        .date(bookmarks.getDate())
                        .book(bookDto)
                        .build();
                bookmarksResponses.add(cartResponse);
            }
            ApiResponse res = new ApiResponse(true, "Fetch books successful!", bookmarksResponses, pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ApiResponse res = new ApiResponse(true, "Fetch books successful!", null, 0, 0, 0,0);
            return ResponseEntity.ok(res);
        }
    }
    private BookDto buildBookDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .title(book.getTitle())
                .qty(book.getQty())
                .price(book.getPrice())
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
}
