package com.ecommerce.ecommerce.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.HomeCategory;
import com.ecommerce.ecommerce.repository.HomeCategoryRepository;
import com.ecommerce.ecommerce.service.HomeCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeCategoryServiceImpl implements HomeCategoryService {

    private final HomeCategoryRepository homeCategoryRepository;

    @Override
    public List<HomeCategory> createCategories(List<HomeCategory> homeCategories) {
        // Get all existing categoryIds from the database
        Set<String> existingCategoryIds = homeCategoryRepository.findAll()
                .stream()
                .map(HomeCategory::getCategoryId)
                .collect(Collectors.toSet());

        // Filter incoming list to remove duplicates based on existing categoryId
        List<HomeCategory> uniqueNewCategories = homeCategories.stream()
                .filter(cat -> !existingCategoryIds.contains(cat.getCategoryId()))
                .collect(Collectors.toList());

        // Save only new unique categories
        homeCategoryRepository.saveAll(uniqueNewCategories);

        // Return updated list
        return homeCategoryRepository.findAll();
    }

    @Override
    public HomeCategory updateCategory(HomeCategory category, Long id) {
        HomeCategory existingCategory = homeCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (category.getImage() != null) {
            existingCategory.setImage(category.getImage());
        }
        if (category.getCategoryId() != null) {
            existingCategory.setCategoryId(category.getCategoryId());
        }

        return homeCategoryRepository.save(existingCategory);
    }

    @Override
    public List<HomeCategory> getAllCategories() {
        return homeCategoryRepository.findAll();
    }
}
