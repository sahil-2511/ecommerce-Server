package com.ecommerce.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.Coupon;
import com.ecommerce.ecommerce.model.User;
       
public interface CouponService {
    Cart applyCoupon(String couponCode,double orderValue, User user) throws Exception;
    Cart removeCoupon(String couponCode, User user);
    Coupon findCouponById(Long couponId);
    Coupon createCoupon(Coupon coupon);
    List <Coupon> findAllCoupons();
    void deleteCoupon(Long Id);


}
