package com.ecommerce.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.model.Wishlist;
import com.ecommerce.ecommerce.service.ProductService;
import com.ecommerce.ecommerce.service.UserService;
import com.ecommerce.ecommerce.service.WishListService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Wishlist> getWishListByUserId(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByToken(jwt);
        Wishlist wishlist = wishListService.getWishlistByUserId(user);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/add-product/{productId}")
    public ResponseEntity<Wishlist> addProductToWishlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long productId) throws Exception {
        Product product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findUserByToken(jwt);
        Wishlist wishlist = wishListService.addProductToWishlist(user, product);
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/remove")
    public ResponseEntity<Wishlist> removeProductFromWishlist(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long productId) throws Exception {
        User user = userService.findUserByToken(jwt);
        Product product = productService.findProductById(productId);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        Wishlist wishlist = wishListService.removeProductFromWishlist(user, product);
        return ResponseEntity.ok(wishlist);
    }
}
