package com.ecommerce.ecommerce.request;

import lombok.Data;

@Data
public class AddItemRequest {
    private String size;
    private Long productId;
    private int quantity;
}
