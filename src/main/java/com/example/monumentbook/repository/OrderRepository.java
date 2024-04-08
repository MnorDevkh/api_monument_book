package com.example.monumentbook.repository;

import com.example.monumentbook.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByUserIdIdAndDeletedFalse(long id, Pageable pageable);

}
