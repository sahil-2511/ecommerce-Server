// package com.ecommerce.ecommerce.model;

// import java.math.BigDecimal;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
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
// public class SellerReport {
//        @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;
//     @OneToOne
//     private Seller seller;
//     private Long totalEarning =0l;
//     private Long totalSales=0l;
//     private BigDecimal totalRefunds =BigDecimal.ZERO;
//     private Long totalTax =0l;
//     private Long netEarnings =0l;
//     private  Integer totalOrders =0;
//     private  Integer cancelOrders =0;
//     private  Integer totalTransaction =0;
  

// }







package com.ecommerce.ecommerce.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SellerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE) // Avoid automatic deletion of Seller
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    private Seller seller;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEarning = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRefunds = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netEarnings = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Column(nullable = false)
    private Integer cancelledOrders = 0; // Removed duplicate field

    @Column(nullable = false)
    private Integer totalTransactions = 0;
}
