package com.example.monumentbook.repository;

import com.example.monumentbook.enums.Action;
import com.example.monumentbook.model.Order;
import com.example.monumentbook.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

    Page<Order> findByUserIdIdAndDeletedFalse(long id, Pageable pageable);
    Page<Order> findAllByAction(Action action,Pageable pageable);
}
