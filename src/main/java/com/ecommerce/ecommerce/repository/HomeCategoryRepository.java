package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.HomeCategory;

public interface HomeCategoryRepository extends JpaRepository<HomeCategory, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find categories by name or other attributes

    
}
