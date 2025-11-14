package com.ecommerce.ecommerce.model;

// package com.ecommerce.ecommerce.model;

// import java.util.HashSet;
// import java.util.Set;

// import jakarta.persistence.CascadeType;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.OneToOne;
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
// public class Cart {
//       @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;
//     @OneToOne 
//     private User user;
//     @OneToMany(mappedBy = "cart",cascade =CascadeType.ALL,orphanRemoval = true )
//     private Set<CartItem>cartItems=new HashSet<>();
//     private double totalSellingPrice;
//     private int totalItems;
//     private Double totalMrpPrice;
//     private  Double discount;
//     private String couponCode;
    


// }

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "cartItems") // Prevent recursion
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id") // Matches your DB column
    private User user;

    
@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonManagedReference
private Set<CartItem> cartItems = new HashSet<>();

    @Column(name = "total_selling_price", nullable = false)
    private double totalSellingPrice;

    @Column(name = "total_items", nullable = false)
    private int totalItems;

    @Column(name = "total_mrp_price")
    private Double totalMrpPrice;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "coupon_code")
    private String couponCode;
}
