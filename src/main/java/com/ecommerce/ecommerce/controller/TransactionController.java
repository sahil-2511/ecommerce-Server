package com.ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.Transaction;
import com.ecommerce.ecommerce.service.SellerService;
import com.ecommerce.ecommerce.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;
    private final SellerService sellerService;

    // Add endpoints for creating and retrieving transactions


@GetMapping("/seller")
public ResponseEntity<List<Transaction>>getTransactionBySeller(@RequestHeader("Authorization")String jwt) throws Exception{
    
  Seller seller = sellerService.getSellerProfile(jwt);
  List <Transaction> transactions = transactionService.getTransactionBySellerId(seller);

  return ResponseEntity.ok(transactions);
    
}


@GetMapping("/all")
public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader("Authorization")String jwt) throws Exception{
    
    Seller seller = sellerService.getSellerProfile(jwt);
    List <Transaction> transactions = transactionService.getAllTransactions();

    return ResponseEntity.ok(transactions);
}
}