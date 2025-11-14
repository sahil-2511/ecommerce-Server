package com.ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.Coupon;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.service.CouponService;
import com.ecommerce.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;
    private final UserService userService;

    // 🎟 Apply or Remove Coupon
    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(
            @RequestParam String apply,
            @RequestParam String code,
            @RequestParam double orderValue,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = extractToken(authorizationHeader);
            User user = userService.findUserByToken(jwt);

            Cart cart = "true".equalsIgnoreCase(apply)
                    ? couponService.applyCoupon(code, orderValue, user)
                    : couponService.removeCoupon(code, user);

            return ResponseEntity.ok(cart);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to apply/remove coupon: " + e.getMessage());
        }
    }

    // 🆕 Create Coupon (Admin only)
    @PostMapping("/admin/create")
    public ResponseEntity<?> createCoupon(
            @RequestBody Coupon coupon,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = extractToken(authorizationHeader);
            User user = userService.findUserByToken(jwt);

            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(403).body("Forbidden: You are not authorized to create coupons.");
            }

            Coupon saved = couponService.createCoupon(coupon);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
        }
    }

    // 📋 Get All Coupons (Admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllCoupons(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = extractToken(authorizationHeader);
            User user = userService.findUserByToken(jwt);

            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(403).body("Forbidden: You are not authorized to view coupons.");
            }

            List<Coupon> coupons = couponService.findAllCoupons();
            return coupons.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(coupons);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
        }
    }

    // ❌ Delete Coupon by ID (Admin only)
    @DeleteMapping("/admin/delete/{couponId}")
    public ResponseEntity<?> deleteCoupon(
            @PathVariable Long couponId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = extractToken(authorizationHeader);
            User user = userService.findUserByToken(jwt);

            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(403).body("Forbidden: You are not authorized to delete coupons.");
            }

            couponService.deleteCoupon(couponId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
        }
    }

    // 🔐 Extract token from Authorization header
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }
}
