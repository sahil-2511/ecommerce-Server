package com.ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.Review;

public interface ReviewRepository    extends JpaRepository<Review, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find reviews by product ID or user ID
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    
}
