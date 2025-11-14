package com.ecommerce.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecommerce.domain.PaymentOrderStatus;
import com.ecommerce.ecommerce.domain.PaymentStatus;
import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.PaymentOrder;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.repository.PaymentOrderRepository;
import com.ecommerce.ecommerce.service.PaymentService;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;

    private final String apiKey = "rzp_test_KneYEsNdn8pkKC";
    private final String apiSecret = "KgUjEuCZOPFiwCEsSCz2IJuG";
    private final String stripeSecreteKey = "sk_test_4eC6v1x4X3g5Y6E"; // Example key
    private final String stripePublicKey = "pk_test_4eC6v1x4X3g5Y6E"; // Example key

    @Override
    @Transactional
    public PaymentOrder createOrder(User user, Set<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("Order set cannot be null or empty");
        }

        // ✅ FIX: Use totalPrice instead of totalSellingPrice (to include discount)
        BigDecimal amount = orders.stream()
                .map(Order::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentOrder.setOrders(new HashSet<>(orders));

        PaymentOrder savedPaymentOrder = paymentOrderRepository.save(paymentOrder);
        for (Order order : orders) {
            orderRepository.save(order);
        }

        return savedPaymentOrder;
    }

    @Override
    public PaymentOrder getPaymentOrderById(String orderId) {
        return paymentOrderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new RuntimeException("Payment Order not found"));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentOrderRepository.findByPaymentLinkId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment Order not found for payment ID: " + paymentId));
    }

    @Override
    @Transactional
    public Boolean proceedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) throws RazorpayException {
        if (paymentOrder == null || paymentOrder.getId() == null) {
            throw new IllegalArgumentException("PaymentOrder or its ID cannot be null.");
        }

        PaymentOrder latestPaymentOrder = getPaymentOrderById(paymentOrder.getId().toString());

        if (latestPaymentOrder.getStatus() == PaymentOrderStatus.PENDING) {
            try {
                RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
                Payment payment = razorpay.payments.fetch(paymentId);
                String status = payment.get("status");

                if ("captured".equalsIgnoreCase(status)) {
                    Set<Order> orders = latestPaymentOrder.getOrders();
                    if (orders != null) {
                        for (Order order : orders) {
                            order.setPaymentStatus(PaymentStatus.COMPLETED);
                            orderRepository.save(order);
                        }
                    }

                    latestPaymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(latestPaymentOrder);
                    return true;
                }

                latestPaymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(latestPaymentOrder);
                return false;

            } catch (RazorpayException e) {
                System.err.println("Error fetching Razorpay payment: " + e.getMessage());
                latestPaymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(latestPaymentOrder);
                return false;
            }
        }
        return false;
    }

    @Override
    public PaymentLink createRazorPaymentLink(User user, BigDecimal amount, Long orderId) throws RazorpayException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be a positive value for Razorpay payment link.");
        }

        BigDecimal amountInPaise = amount.multiply(new BigDecimal("100"));
        long finalAmountLong;

        try {
            finalAmountLong = amountInPaise.longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Amount has too many decimal places for Razorpay.", e);
        }

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", finalAmountLong);
        paymentLinkRequest.put("currency", "INR");

        JSONObject customer = new JSONObject();
        customer.put("name", user.getFullname());
        customer.put("email", user.getEmail());
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("callback_url", "http://localhost:5173/payment-success/" + orderId);
        paymentLinkRequest.put("callback_method", "get");

        return razorpay.paymentLink.create(paymentLinkRequest);
    }

    @Override
    public String createStripePaymentLink(User user, Long amount, Long orderId) {
        try {
            Stripe.apiKey = stripeSecreteKey;

            if (amount == null || amount < 0) {
                throw new IllegalArgumentException("Stripe amount must be non-negative.");
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:3000/payment-success/" + orderId)
                    .setCancelUrl("http://localhost:3000/payment-failure/" + orderId)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Big Bazaar Payment")
                                                    .build())
                                            .setUnitAmount(amount * 100)
                                            .build())
                                    .build())
                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe payment link: " + e.getMessage(), e);
        }
    }
}
