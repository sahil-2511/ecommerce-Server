package com.ecommerce.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.PaymentOrder;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.SellerReport;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.response.ApiResoponse;
import com.ecommerce.ecommerce.response.PaymentLinkResponse;
import com.ecommerce.ecommerce.service.OrderService;
import com.ecommerce.ecommerce.service.PaymentService;
import com.ecommerce.ecommerce.service.SellerReportService;
import com.ecommerce.ecommerce.service.SellerService;
import com.ecommerce.ecommerce.service.TransactionService;
import com.ecommerce.ecommerce.service.UserService;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final OrderService orderService;
    private final TransactionService transactionService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResoponse> paymentSuccessHandler(
            @PathVariable String paymentId,
            @RequestParam String paymentLinkId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        // Retrieve user from JWT token
        User user = userService.findUserByToken(jwt);
 
        PaymentLinkResponse paymentLinkResponse;
       PaymentOrder   paymentOrder  = paymentService.getPaymentOrderByPaymentId(paymentLinkId);
       boolean isPaymentSuccessful = paymentService.proceedPayment(paymentOrder, paymentId, paymentLinkId);
       if(isPaymentSuccessful) {
        for (Order order : paymentOrder.getOrders()) {
   
            transactionService.createTransaction(order);
            Seller seller =sellerService.getSellerById(order.getSellerId());
            SellerReport report = sellerReportService.getSellerReport(seller);
            report.setTotalOrders(report.getTotalOrders() + 1);
            report.setTotalEarning(report.getTotalEarning().add(order.getTotalSellingPrice()));
            report.setTotalSales(report.getTotalSales().add(order.getTotalSellingPrice()));
            sellerReportService.updateSellerReport(report);



            
        
        }}  
        ApiResoponse response = new ApiResoponse();
        response.setMessage("payment successfully");
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
