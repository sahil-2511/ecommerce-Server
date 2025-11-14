package com.ecommerce.ecommerce.service.impl;

import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.Transaction;

import com.ecommerce.ecommerce.repository.SellerRepository;
import com.ecommerce.ecommerce.repository.TransactionRepository;
import com.ecommerce.ecommerce.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    @Override
    public Transaction createTransaction(Order order) {
        Seller seller = sellerRepository.findById(order.getSellerId()).get();
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setSeller(seller);
        transaction.setCustomer(order.getUser());
        transaction.setDate(LocalDateTime.now());

 

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionBySellerId(Seller seller) {
        return transactionRepository.findBySellerId(seller.getId());
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
