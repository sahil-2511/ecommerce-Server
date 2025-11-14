




package com.ecommerce.ecommerce.model;

import java.math.BigDecimal;
import java.util.HashSet;

import java.util.Set;

import com.ecommerce.ecommerce.domain.paymentMethod;
import com.ecommerce.ecommerce.domain.PaymentOrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; 

    private BigDecimal amount;

 
    private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

  
    private paymentMethod paymentMethod;

   
    @ManyToOne
    private User user;
  

    private String paymentLinkId;
@OneToMany
    private  Set <Order> orders= new HashSet<>();
}



