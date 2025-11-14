package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find a wishlist by user ID:
    // List<Wishlist> findByUserId(Long userId);
    Wishlist findByUserId(Long userId);
    
}
