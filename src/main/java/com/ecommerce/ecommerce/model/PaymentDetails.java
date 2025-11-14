package com.ecommerce.ecommerce.model;

import com.ecommerce.ecommerce.domain.PaymentStatus;

import lombok.Data;

@Data
public class PaymentDetails {
private String paymentId;
private String razorpayPaymentLinkId;
private String razorpayPaymentLinkreferenceId;
private String razorpayPaymentLinkStatus;
private String razorpayPaymentIdZWSP;
private PaymentStatus status;
}
