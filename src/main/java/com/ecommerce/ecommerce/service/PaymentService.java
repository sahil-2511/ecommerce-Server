package com.ecommerce.ecommerce.service;

import java.math.BigDecimal;
import java.util.Set;
import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.PaymentOrder;
import com.ecommerce.ecommerce.model.User;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;

public interface PaymentService {
    PaymentOrder createOrder(User user, Set<Order> orders);
    PaymentOrder getPaymentOrderById(String orderId);
    PaymentOrder getPaymentOrderByPaymentId(String  orderId);
    Boolean proceedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) throws RazorpayException;
    PaymentLink createRazorPaymentLink(User user, BigDecimal amount, Long orderId) throws RazorpayException;
    String createStripePaymentLink(User user, Long amount, Long orderId);
}
