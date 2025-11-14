package com.ecommerce.ecommerce.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
  
    private List<String> colors;
    private Integer quantity; // Changed from String to Integer
    private List<String> images;
    private String category;
    private String category2;
    private String category3;
    private List<String> sizes;  
    private BigDecimal mrpPrice;
     private BigDecimal sellingPrice;

    
  
}
