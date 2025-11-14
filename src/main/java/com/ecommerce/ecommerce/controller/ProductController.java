package com.ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.exception.ProductException;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) throws ProductException {
        Product product = productService.findProductById(productId);
        return ResponseEntity.ok(product);
    }

   @GetMapping("/search")
public ResponseEntity<List<Product>> searchProducts(
        @RequestParam("q") String query,   // ✅ match 'q' from frontend
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    List<Product> results = productService.searchProducts(query, pageable);
    return ResponseEntity.ok(results);
}


    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false, defaultValue = "id") String sort, // ✅ FIXED
            @RequestParam(required = false) String stock,
            @RequestParam(defaultValue ="0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
    
        Page<Product> products = productService.getAllProducts(
                category, description, brand, 
                color != null ? List.of(color) : null, sizes, 
                minPrice, maxPrice, minDiscount, sort, stock, pageNo
        );
    
        return ResponseEntity.ok(products);
    }
    
}
