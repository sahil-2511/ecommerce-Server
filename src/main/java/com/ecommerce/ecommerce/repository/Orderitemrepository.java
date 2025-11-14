package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.model.OrderItem;

public interface Orderitemrepository extends JpaRepository <OrderItem, Long>{

    
}
