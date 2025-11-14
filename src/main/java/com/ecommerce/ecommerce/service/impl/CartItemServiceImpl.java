package com.ecommerce.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.CartItem;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.CartItemRepository;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.service.CartItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public CartItem updateCartItem(Long userId, Long cartItemId, CartItem updatedCartItem) {
        CartItem item = findCartItemById(cartItemId);
        if (item == null) throw new RuntimeException("Cart item not found.");

        User cartItemUser = item.getCart().getUser();
        if (!cartItemUser.getId().equals(userId)) {
            throw new RuntimeException("Unauthorized update attempt.");
        }

        item.setQuantity(updatedCartItem.getQuantity());
        item.setMrpPrice(item.getProduct().getMrpPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setSellingPrice(item.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        CartItem savedItem = cartItemRepository.save(item);
        recalculateCartTotals(item.getCart());

        return savedItem;
    }

  @Override
@Transactional
public void removeCartItem(Long userId, Long cartItemId) {
    CartItem item = findCartItemById(cartItemId);
    if (!item.getCart().getUser().getId().equals(userId)) {
        throw new RuntimeException("Unauthorized delete attempt.");
    }

    Cart cart = item.getCart();

    // Remove reference from cart
    cart.getCartItems().remove(item);

    // Delete the item from repository
    cartItemRepository.delete(item);

    // Recalculate totals (AFTER deletion)
    recalculateCartTotals(cart);
}

    @Override
    public CartItem findCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found."));
    }

    private void recalculateCartTotals(Cart cart) {
        BigDecimal totalMrp = BigDecimal.ZERO;
        BigDecimal totalSelling = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getCartItems()) {
            BigDecimal mrp = Optional.ofNullable(item.getMrpPrice()).orElse(BigDecimal.ZERO);
            BigDecimal selling = Optional.ofNullable(item.getSellingPrice()).orElse(BigDecimal.ZERO);
            totalMrp = totalMrp.add(mrp);
            totalSelling = totalSelling.add(selling);
            totalItems += item.getQuantity();
        }

        cart.setTotalMrpPrice(totalMrp.doubleValue());
        cart.setTotalSellingPrice(totalSelling.doubleValue());
        cart.setTotalItems(totalItems);

        // Reset coupon if total drops below previous threshold
        cartRepository.save(cart);
    }
}
