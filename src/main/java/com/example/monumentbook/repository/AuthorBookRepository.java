package com.example.monumentbook.repository;

import com.example.monumentbook.model.AuthorBook;
import com.example.monumentbook.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorBookRepository extends JpaRepository<AuthorBook, Integer> {
        List<AuthorBook> findAllByBook(Book book);
}
