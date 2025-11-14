package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.model.CartItem;

public interface  CartItemService {
    CartItem updateCartItem(Long UserId, Long Id ,CartItem cartItem);
    void removeCartItem(Long userId,Long cartItemId);
    CartItem findCartItemById(Long id);
}
