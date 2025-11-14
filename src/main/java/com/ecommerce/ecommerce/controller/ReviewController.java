package com.ecommerce.ecommerce.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Review;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.request.CreateReviewRequest;
import com.ecommerce.ecommerce.service.ProductService;
import com.ecommerce.ecommerce.service.ReviewService;
import com.ecommerce.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    // ✅ Get all reviews for a product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // ✅ Create a review
    @PostMapping("/product/{productId}/create")
    public ResponseEntity<Review> createReview(@PathVariable Long productId, 
                                               @RequestBody CreateReviewRequest req, 
                                               @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByToken(jwt);
        Product product = productService.findProductById(productId);
        Review review = reviewService.createReview(req, user, product);
        return ResponseEntity.ok(review);
    }

@PutMapping("/review/{reviewId}/update")
public ResponseEntity<Review> updateReview(@PathVariable Long reviewId,
                                           @RequestBody CreateReviewRequest req,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
    User user = userService.findUserByToken(jwt);
    Review updatedReview = reviewService.updateReview(reviewId, req.getReviewText(), req.getReviewRating(), user.getId());
    return ResponseEntity.ok(updatedReview);
}

@DeleteMapping("/review/{reviewId}/delete")
public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, 
                                           @RequestHeader("Authorization") String jwt) throws Exception {
    User user = userService.findUserByToken(jwt);
    reviewService.deleteReview(reviewId, user.getId()); // reviewId used here
    return ResponseEntity.ok("Review deleted successfully");
}

    // ✅ Get a specific review by ID
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) throws Exception {
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    // ✅ Get all reviews for a product by a specific user
    @GetMapping("/product/{productId}/user/{userId}")
    public ResponseEntity<List<Review>> getAllReviewsByUser(@PathVariable Long productId, 
                                                            @PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewByProductId( productId);
        return ResponseEntity.ok(reviews);
    }
}
