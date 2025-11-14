package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.Deal;

public interface DealRepository  extends JpaRepository<Deal, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find deals by name or other attributes
    
    
}
