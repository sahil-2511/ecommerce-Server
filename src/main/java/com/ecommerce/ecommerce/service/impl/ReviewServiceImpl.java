package com.ecommerce.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Review;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.ReviewRepository;
import com.ecommerce.ecommerce.request.CreateReviewRequest;
import com.ecommerce.ecommerce.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;
    @Override
    public Review createReview(CreateReviewRequest Req, User user, Product product) {
        Review review = new Review();
        review.setReviewText(Req.getReviewText());
        review.setRating(Req.getReviewRating());
        review.setUser(user);
        review.setProduct(product);
        review.setProductImages(Req.getProductImage());
        product.getReviews().add(review);
        return reviewRepository.save(review);
    }
        

    @Override
    public List<Review> getReviewByProductId(Long productId){
        return reviewRepository.findByProductId(productId);
        }
    @Override
    public Review updateReview(Long reviewId, String reviewText, double rating, Long userId) {
        Review review = getReviewById(reviewId);
        if (review.getUser().getId().equals(userId)) {
            review.setReviewText(reviewText);
            review.setRating(rating);
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("You are not authorized to update this review");
        }
      
}
    @Override
    public void deleteReview(Long reviewId, Long userid){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (review.getUser().getId().equals(userid)) {
            reviewRepository.delete(review);
        } else {
            throw new RuntimeException("You are not authorized to delete this review");
        }
    }


    @Override
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
    }
    
}
