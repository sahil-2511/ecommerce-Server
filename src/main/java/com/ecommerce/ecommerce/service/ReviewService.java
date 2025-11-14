package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Review;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.request.CreateReviewRequest;

public  interface ReviewService {
    Review createReview(CreateReviewRequest Req,User user, Product product);
    List<Review> getReviewByProductId(Long productId);
    Review   updateReview(Long  reviewId, String reviewText, double rating,Long userId );
    void deleteReview(Long reviewId, Long userid);
     Review getReviewById(Long reviewId);

    
}
