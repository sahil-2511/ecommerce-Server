package com.ecommerce.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.Coupon;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.repository.CouponRepository;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.service.CouponService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
@Override
public Cart applyCoupon(String couponCode, double ignoredOrderValue, User user) throws Exception {
    Coupon coupon = couponRepository.findByCode(couponCode);
    if (coupon == null) {
        throw new Exception("Coupon is not valid");
    }

    Cart cart = cartRepository.findByUserId(user.getId());
    if (cart == null) {
        throw new Exception("Cart not found for user");
    }

    if (user.getUsedCoupons().contains(coupon)) {
        throw new Exception("Coupon already used by this user");
    }

    BigDecimal currentOrder = BigDecimal.valueOf(cart.getTotalSellingPrice());
    if (currentOrder.compareTo(coupon.getMinimumOrderValue()) < 0) {
        throw new Exception("Valid only for minimum order value: " + coupon.getMinimumOrderValue());
    }

    LocalDate today = LocalDate.now();
    if (coupon.isActive()
            && (today.isEqual(coupon.getValidateStartDate()) || today.isAfter(coupon.getValidateStartDate()))
            && (today.isEqual(coupon.getValidateEndDate()) || today.isBefore(coupon.getValidateEndDate()))) {

        BigDecimal discount = currentOrder
                .multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100));

        BigDecimal discountedTotal = currentOrder.subtract(discount);

        cart.setDiscount(discount.doubleValue());
        cart.setTotalSellingPrice(discountedTotal.doubleValue());
        cart.setCouponCode(couponCode);

        user.getUsedCoupons().add(coupon);
        userRepository.save(user);

        System.out.println("=== COUPON APPLIED ===");
        System.out.println("Original Price: " + currentOrder);
        System.out.println("Discount %: " + coupon.getDiscountPercentage());
        System.out.println("Discount Amount: " + discount);
        System.out.println("New Total: " + discountedTotal);


Cart savedCart = cartRepository.save(cart);
cartRepository.flush(); // <-- Forces DB sync
return savedCart;

    }

    throw new Exception("Coupon is not active or has expired");
}

    @Override
    public Cart removeCoupon(String couponCode, User user) {
        Coupon coupon = couponRepository.findByCode(couponCode);
        if (coupon == null) {
            throw new RuntimeException("Coupon not found");
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        // Restore original totalSellingPrice by reversing the discount
        BigDecimal discountedPrice = BigDecimal.valueOf(cart.getTotalSellingPrice());
        BigDecimal discount = BigDecimal.valueOf(cart.getDiscount());
        BigDecimal restoredTotal = discountedPrice.add(discount);

        cart.setDiscount(0.0);
        cart.setTotalSellingPrice(restoredTotal.doubleValue());
        cart.setCouponCode(null);

        user.getUsedCoupons().remove(coupon);
        userRepository.save(user);

        return cartRepository.save(cart);
    }

    @Override
    public Coupon findCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().isEmpty()) {
            throw new IllegalArgumentException("Coupon code cannot be null or empty");
        }

        if (coupon.getDiscountPercentage() <= 0 || coupon.getDiscountPercentage() > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 1 and 100");
        }

        if (coupon.getValidateStartDate() == null || coupon.getValidateEndDate() == null) {
            throw new IllegalArgumentException("Validation dates cannot be null");
        }

        if (coupon.getValidateEndDate().isBefore(coupon.getValidateStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (coupon.getMinimumOrderValue() == null || coupon.getMinimumOrderValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Minimum order value must be greater than zero");
        }

        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCoupon(Long id) {
        findCouponById(id); // Ensure coupon exists
        couponRepository.deleteById(id);
    }
}
