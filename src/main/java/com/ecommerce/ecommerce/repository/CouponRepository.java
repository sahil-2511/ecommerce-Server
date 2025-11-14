package com.ecommerce.ecommerce.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find coupons by code or expiration date
    Coupon findByCode(String code);
    List<Coupon> findByExpirationDateBefore(LocalDate date);
    
}
