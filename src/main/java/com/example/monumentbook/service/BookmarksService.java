package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.BookmarksRequest;
import com.example.monumentbook.model.requests.CartRequest;
import com.example.monumentbook.model.requests.CartUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface BookmarksService {
    ResponseEntity<?> addToBookmarks(BookmarksRequest bookmarksRequest);
    ResponseEntity<?> getBookmarks(Integer page, Integer size);
//    ResponseEntity<?> getBookmarksById(Integer id);
//    ResponseEntity<?> updateCartById(Integer id, CartUpdateRequest cartRequest);
    ResponseEntity<?> deleteCartById(Integer id);
    ResponseEntity<?> findBookmarksByUser(Integer page, Integer size);

}
