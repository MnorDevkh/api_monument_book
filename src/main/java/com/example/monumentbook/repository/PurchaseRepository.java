package com.example.monumentbook.repository;

import com.example.monumentbook.model.Purchase;
import com.example.monumentbook.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findAllBySupplier(Supplier supplier);

    Page<Purchase> findAll(Pageable pageable);
}
