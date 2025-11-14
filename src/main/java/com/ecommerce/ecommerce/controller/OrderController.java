package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.domain.OrderStatus;
import com.ecommerce.ecommerce.domain.paymentMethod;
import com.ecommerce.ecommerce.model.*;
import com.ecommerce.ecommerce.repository.PaymentOrderRepository;
import com.ecommerce.ecommerce.response.PaymentLinkResponse;
import com.ecommerce.ecommerce.service.*;

import com.razorpay.PaymentLink;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final SellerReportService sellerReportService;
    private final SellerService sellerService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;

    @PostMapping("/create")
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(@RequestBody Address shippingAddress,
                                                                  @RequestParam paymentMethod paymentMethod,
                                                                  @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByToken(jwt);
        Cart cart = cartService.findUsercart(user);

        // Create orders and get order set
        Set<Order> orders = orderService.CreateOrder(user, shippingAddress, cart);

        // Calculate total price (after discount) across all seller orders
        BigDecimal totalPayable = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create paymentOrder record
        PaymentOrder paymentOrder = paymentService.createOrder(user, orders);
        paymentOrder.setAmount(totalPayable);
        paymentOrderRepository.save(paymentOrder);

        PaymentLinkResponse res = new PaymentLinkResponse();

        if (paymentMethod.equals(paymentMethod.RAZORPAY)) {
            // Create Razorpay Payment Link
            PaymentLink payment = paymentService.createRazorPaymentLink(user, totalPayable, paymentOrder.getId());

            String paymentUrl = payment.get("short_url");
            String paymentId = payment.get("id");

            res.setPayment_link_url(paymentUrl);
            res.setPayment_link_id(paymentId);

            // Store Razorpay payment link ID
            paymentOrder.setPaymentLinkId(paymentId);
            paymentOrderRepository.save(paymentOrder);

        } else if (paymentMethod.equals(paymentMethod.STRIPE)) {
            // Create Stripe session link
            String stripeUrl = paymentService.createStripePaymentLink(user, totalPayable.longValue(), paymentOrder.getId());

            res.setPayment_link_url(stripeUrl);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByToken(jwt);
        Order order = orderService.findOrderBy(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/user/history")
    public ResponseEntity<List<Order>> getUserOrderHistory(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByToken(jwt);
        List<Order> orders = orderService.userOrderHistory(user.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Order>> getSellerOrders(@PathVariable Long sellerId) {
        List<Order> orders = orderService.sellerOrder(sellerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long orderItemId,
                                                      @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByToken(jwt);
        OrderItem orderItem = orderService.getOrderItemById(orderItemId);
        return ResponseEntity.ok(orderItem);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId,
                                             @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByToken(jwt);
        Order order = orderService.cancelOrder(orderId, user);

        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or already canceled");
        }

        Seller seller = sellerService.getSellerById(order.getSellerId());
        if (seller == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found");
        }

        // Update Seller Report
        SellerReport report = sellerReportService.getSellerReport(seller);
        report.setCancelledOrders(report.getCancelledOrders() + 1);
        report.setTotalRefunds(report.getTotalRefunds().add(order.getTotalPrice())); // updated to use totalPrice

        sellerReportService.updateSellerReport(report);

        return ResponseEntity.ok(order);
    }
}
