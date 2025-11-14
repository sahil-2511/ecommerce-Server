// package com.ecommerce.ecommerce.repository;

// import org.springframework.data.jpa.repository.JpaRepository;

// import com.ecommerce.ecommerce.model.Order;
// import com.ecommerce.ecommerce.model.PaymentOrder;

// public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

//     PaymentOrder findByOrder(Order order);
//     // Changed to Long if orderId is the primary key

//     // Optional<PaymentOrder> findByPaymentLinkedId(String paymentLinkedId); // Ensuring field name matches entity

//     PaymentOrder findByPaymentLinkId(String paymentLinkId);
//     PaymentOrder findByRazorpayPaymentLink(String razorpayPaymentLink); // Ensuring correctness
// }





package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.ecommerce.ecommerce.model.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByPaymentLinkId(String paymentLinkId); // ✅ Matches entity field

    // ❌ Removed findByRazorpayPaymentLink (Field doesn't exist)

}
