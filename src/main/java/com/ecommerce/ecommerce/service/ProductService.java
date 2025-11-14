package com.ecommerce.ecommerce.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.ecommerce.exception.ProductException;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.request.CreateProductRequest;

public interface ProductService {
    
    Product createProduct(CreateProductRequest req, Seller seller);

    
    
    void deleteProduct(Long productId) throws Exception;
    
    Product updateProduct(Long productId, Product product) throws ProductException;
    
    Product findProductById(Long productId) throws ProductException;
    
    List<Product> searchProducts(String query, Pageable pageable);  // Changed return type to Page<Product>
    
    Page<Product> getAllProducts(
        String category,
        String description,
        String brand,
     List<String> color,
        List<String> sizes,
        Integer minPrice,
        Integer maxPrice,
        Integer minDiscount,
        String sort,
        String stock,
        Integer pageNo
    );

    Page<Product> getProductBySeller_Id(Long sellerId, Pageable pageable);


    
    
}
