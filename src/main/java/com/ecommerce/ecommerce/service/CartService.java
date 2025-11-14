package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.CartItem;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.User;

public interface CartService {
    // Add a new item to the cart
    CartItem addCartItem(User user, Product product, String size, int quantity) throws Exception;

    // Retrieve the cart of a user
    Cart findUsercart(User user);

   
}
