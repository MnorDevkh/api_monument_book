package com.example.monumentbook.repository;

import com.example.monumentbook.model.Purchase;
import com.example.monumentbook.model.PurchaseItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItems, Integer> {
    List<PurchaseItems> findByPurchaseId(Integer id);
}
