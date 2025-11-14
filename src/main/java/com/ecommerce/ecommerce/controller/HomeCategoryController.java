package com.ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.model.Home;
import com.ecommerce.ecommerce.model.HomeCategory;
import com.ecommerce.ecommerce.service.HomeCategoryService;
import com.ecommerce.ecommerce.service.HomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HomeCategoryController {

    private final HomeCategoryService homeCategoryService;
    private final HomeService homeService;

    @PostMapping("/home/categories")
    public ResponseEntity<Home> createHomeCategories(
            @RequestBody List<HomeCategory> homeCategories) {
        List<HomeCategory> categories = homeCategoryService.createCategories(homeCategories);
        Home home = homeService.createHomepageData(categories);
        return new ResponseEntity<>(home, HttpStatus.ACCEPTED);
    }

    @GetMapping("admin/home-category")
    public ResponseEntity<List<HomeCategory>> getAllCategories(
            @RequestHeader("Authorization") String token) {
        List<HomeCategory> categories = homeCategoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PatchMapping("/admin/home-category/{id}")
    public ResponseEntity<HomeCategory> updateCategory(
            @RequestHeader("Authorization") String token,
            @RequestBody HomeCategory homeCategory,
            @PathVariable Long id) {
        HomeCategory updatedCategory = homeCategoryService.updateCategory(homeCategory, id);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }
}
