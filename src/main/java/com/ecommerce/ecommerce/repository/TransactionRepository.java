package com.ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.Transaction;

public interface TransactionRepository  extends JpaRepository< Transaction ,Long>{
    List<Transaction> findBySellerId(long sellerId);
    
}
