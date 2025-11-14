package com.ecommerce.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Coupon {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private Double discountPercentage; // Fixed typo and changed type to Double
    private LocalDate expirationDate;
    private LocalDate validateStartDate; // Changed from String to LocalDate
    private LocalDate validateEndDate;   // Changed from String to LocalDate

    private BigDecimal minimumOrderValue; // Changed from String to BigDecimal for better precision

    private boolean isActive = true;

    @ManyToMany(mappedBy = "usedCoupons")
    private Set<User> usedByUser = new HashSet<>();
}
