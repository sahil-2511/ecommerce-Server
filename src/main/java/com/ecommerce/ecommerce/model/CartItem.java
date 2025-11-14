// package com.ecommerce.ecommerce.model;

// import java.math.BigDecimal;

// import com.fasterxml.jackson.annotation.JsonIgnore;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.ManyToOne;
// import lombok.AllArgsConstructor;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// @EqualsAndHashCode
// public class CartItem {
//       @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;
//     @ManyToOne
//     @JsonIgnore
//     private Cart cart;
//     @ManyToOne
//     private Product product;
//     private String size;
//     private int quantity=1;
//     private BigDecimal mrpPrice;
//     private BigDecimal sellingPrice;
//     private Long userId;


// }


package com.ecommerce.ecommerce.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "cart")  // Prevent recursion in Lombok
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore  // Prevent infinite recursion during serialization
    private Cart cart;

    @ManyToOne
    private Product product;

    private String size;
    private int quantity = 1;
    
    private BigDecimal mrpPrice = BigDecimal.ZERO;
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private Long userId;
}
