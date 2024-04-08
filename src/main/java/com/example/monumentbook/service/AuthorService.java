package com.example.monumentbook.service;

import com.example.monumentbook.model.requests.AuthorRequest;
import com.example.monumentbook.model.requests.CategoryRequest;
import org.springframework.http.ResponseEntity;

public interface AuthorService {
    ResponseEntity<?> add(AuthorRequest authorRequest);
    ResponseEntity<?> getById(Integer id);
    ResponseEntity<?> getAll(Integer page, Integer size);
    ResponseEntity<?> update(Integer id, AuthorRequest authorRequest);
    ResponseEntity<?> deleted(Integer id);
    ResponseEntity<?> addFeature(Integer id);
    ResponseEntity<?> getFeature(Integer page, Integer size);

}
