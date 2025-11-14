package com.ecommerce.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.ecommerce.domain.USER_ROLE;
import com.ecommerce.ecommerce.model.Deal;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.response.ApiResoponse;

import com.ecommerce.ecommerce.service.DealService;
import com.ecommerce.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/deals")
public class DealController {

    private final DealService dealService;
    private final UserService userService;

    // ✅ Helper method to check if the user is admin
    private boolean isAdmin(User user) {
        return user.getRole() == USER_ROLE.ADMIN;
    }

    private User getUserFromJwt(String jwt) throws Exception {
        String token = jwt.startsWith("Bearer ") ? jwt.substring(7) : jwt;
        return userService.findUserByToken(token);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDeals(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = getUserFromJwt(jwt);
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only.");
        }

        return ResponseEntity.ok(dealService.getDeals());
    }

    @PostMapping
    public ResponseEntity<?> createDeals(@RequestBody Deal deal,
                                         @RequestHeader("Authorization") String jwt) throws Exception {
        User user = getUserFromJwt(jwt);
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only.");
        }

        Deal createdDeal = dealService.createDeal(deal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeal);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateDeal(@PathVariable Long id,
                                        @RequestBody Deal deal,
                                        @RequestHeader("Authorization") String jwt) throws Exception {
        User user = getUserFromJwt(jwt);
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only.");
        }

        Deal updatedDeal = dealService.updateDeal(deal, id);
        return ResponseEntity.ok(updatedDeal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResoponse> deleteDeal(@PathVariable Long id,
                                                  @RequestHeader("Authorization") String jwt) throws Exception {
        User user = getUserFromJwt(jwt);
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        dealService.deleteDeal(id);

        ApiResoponse response = new ApiResoponse();
        response.setMessage("✅ Deal deleted successfully.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
