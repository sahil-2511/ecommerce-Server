package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.Transaction;

public interface TransactionService {
    Transaction createTransaction(Order order);
    List<Transaction>getTransactionBySellerId(Seller seller);
    List<Transaction>getAllTransactions();

}
