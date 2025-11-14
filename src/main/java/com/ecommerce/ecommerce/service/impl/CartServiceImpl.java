package com.ecommerce.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.CartItem;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.CartItemRepository;
import com.ecommerce.ecommerce.repository.CartRepository;
import com.ecommerce.ecommerce.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public CartItem addCartItem(User user, Product product, String size, int quantity) {
        // Ensure the cart exists
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        // Check if the item already exists in the cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            cartItem.setUserId(user.getId());
            cart.getCartItems().add(cartItem);
        }

        // Update selling price
        cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        
        cartItem = cartItemRepository.save(cartItem); // Save cart item
        cartRepository.save(cart); // Update cart to prevent duplicate inserts

        return cartItem;
    }
@Override
public Cart findUsercart(User user) {
    Cart cart = cartRepository.findByUserId(user.getId());
    if (cart == null) {
        cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

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
    cart.setTotalItems(totalItems);

    // ⛔️ Do NOT override coupon-based discount
    if (cart.getCouponCode() == null || cart.getCouponCode().isEmpty()) {
        // If no coupon is applied, calculate normal discount
        double discount = calculateDiscountPercent(totalMrp, totalSelling);
        cart.setDiscount(discount);
        cart.setTotalSellingPrice(totalSelling.doubleValue());
    }

    return cartRepository.save(cart);
}

    private double calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
        if (mrpPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return mrpPrice.subtract(sellingPrice)
                .divide(mrpPrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}


















































































































// package com.ecommerce.ecommerce.service.impl;

// import java.math.BigDecimal;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.CartItem;
// import com.ecommerce.ecommerce.model.Product;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.repository.CartItemRepository;
// import com.ecommerce.ecommerce.repository.CartRepository;
// import com.ecommerce.ecommerce.service.CartService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CartServiceImpl implements CartService {
//     private final CartRepository cartRepository;
//     private final CartItemRepository cartItemRepository;

//     @Override
//     @Transactional
//     public CartItem addCartItem(User user, Product product, String size, int quantity) {
//       //
//         Cart cart = findUsercart(user);
//         if (cart == null) {
//             cart = new Cart();
//             cart.setUser(user);
//             cartRepository.save(cart);
//         }

//         Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

//         CartItem cartItem;
//         if (existingCartItem.isPresent()) {
//             cartItem = existingCartItem.get();
//             cartItem.setQuantity(cartItem.getQuantity() + quantity);
//             cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
//         } else {
//             cartItem = new CartItem();
//             cartItem.setProduct(product);
//             cartItem.setSize(size);
//             cartItem.setQuantity(quantity);

//             cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));

//         //


//             cartItem.setUserId(user.getId());
//             cartItem = cartItemRepository.save(cartItem);
//             cart.getCartItems().add(cartItem);
//         }
//         cartRepository.save(cart);
//         return cartItemRepository.save(cartItem);
//     }



//     @Override
//     public Cart findUsercart(User user) {
//         Cart cart = cartRepository.findByUserId(user.getId());
        
//         if (cart == null || cart.getCartItems().isEmpty()) {
//             return new Cart();
//         }

//         BigDecimal totalPrice = BigDecimal.ZERO;
//         BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
//         int totalItems = 0;

//         for (CartItem cartItem : cart.getCartItems()) {
//             totalPrice = totalPrice.add(cartItem.getMrpPrice());
//             totalDiscountedPrice = totalDiscountedPrice.add(cartItem.getSellingPrice());
//             totalItems += cartItem.getQuantity();
//         }

//         cart.setTotalMrpPrice(totalPrice.doubleValue());
//         cart.setTotalSellingPrice(totalDiscountedPrice.doubleValue());
//         cart.setDiscount(calculateDiscountPercent(totalPrice, totalDiscountedPrice));
//         cart.setTotalItems(totalItems);

//         cartRepository.save(cart);
//         return cart;
//     }

//     private double calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
//         if (mrpPrice.compareTo(BigDecimal.ZERO) == 0) {
//             return 0.0;
//         }
//         return mrpPrice.subtract(sellingPrice)
//                 .divide(mrpPrice, 2, BigDecimal.ROUND_HALF_UP)
//                 .multiply(BigDecimal.valueOf(100))
//                 .doubleValue();
//     }
// }




















































// package com.ecommerce.ecommerce.service.impl;

// import java.math.BigDecimal;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.CartItem;
// import com.ecommerce.ecommerce.model.Product;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.repository.CartItemRepository;
// import com.ecommerce.ecommerce.repository.CartRepository;
// import com.ecommerce.ecommerce.service.CartService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CartServiceImpl implements CartService {
//     private final CartRepository cartRepository;
//     private final CartItemRepository cartItemRepository;

//     @Override
//     @Transactional
//     public CartItem addCartItem(User user, Product product, String size, int quantity) {
//         Cart cart = cartRepository.findByUserId(user.getId());
//         if (cart == null) {
//             cart = new Cart();
//             cart.setUser(user);
//             cart.setTotalItem(0); // Ensure default value is set
//             cart.setTotalMrpPrice(0.0);
//             cart.setTotalSellingPrice(0.0);
//             cart.setDiscount(0.0);
//             cartRepository.save(cart);
//         }

//         Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

//         CartItem cartItem;
//         if (existingCartItem.isPresent()) {
//             cartItem = existingCartItem.get();
//             cartItem.setQuantity(cartItem.getQuantity() + quantity);
//             cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
//         } else {
//             cartItem = new CartItem();
//             cartItem.setProduct(product);
//             cartItem.setCart(cart);
//             cartItem.setSize(size);
//             cartItem.setQuantity(quantity);
//             cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));

//             cartItem = cartItemRepository.save(cartItem);
//             cart.getCartItems().add(cartItem);
//         }

//         updateCartTotals(cart); // Update cart totals before saving
//         cartRepository.save(cart);
//         return cartItemRepository.save(cartItem);
//     }

//     @Override
//     @Transactional
//     public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
//         Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
//         if (cartItemOptional.isEmpty()) {
//             throw new RuntimeException("CartItem not found");
//         }

//         CartItem cartItem = cartItemOptional.get();

//         if (quantity <= 0) {
//             deleteCartItem(cartItemId);
//             return null;
//         }

//         cartItem.setQuantity(quantity);
//         cartItem.setSellingPrice(cartItem.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(quantity)));

//         cartItem = cartItemRepository.save(cartItem);
//         updateCartTotals(cartItem.getCart()); // Update cart totals after quantity change
//         return cartItem;
//     }

//     @Override
//     @Transactional
//     public void deleteCartItem(Long cartItemId) {
//         Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
//         if (cartItemOptional.isEmpty()) {
//             throw new RuntimeException("CartItem not found");
//         }

//         CartItem cartItem = cartItemOptional.get();
//         Cart cart = cartItem.getCart();
//         cart.getCartItems().remove(cartItem);
//         cartItemRepository.delete(cartItem);

//         updateCartTotals(cart); // Update cart totals after item removal

//         if (cart.getCartItems().isEmpty()) {
//             cartRepository.delete(cart); // Delete cart if empty
//         } else {
//             cartRepository.save(cart);
//         }
//     }

//     @Override
//     @Transactional
//     public void clearCart(User user) {
//         Cart cart = cartRepository.findByUserId(user.getId());
//         if (cart != null) {
//             cartItemRepository.deleteAll(cart.getCartItems());
//             cartRepository.delete(cart);
//         }
//     }

//     @Override
//     public Cart findUsercart(User user) {
//         Cart cart = cartRepository.findByUserId(user.getId());

//         if (cart == null || cart.getCartItems().isEmpty()) {
//             return new Cart();
//         }

//         updateCartTotals(cart);
//         cartRepository.save(cart);
//         return cart;
//     }

//     private void updateCartTotals(Cart cart) {
//         BigDecimal totalPrice = BigDecimal.ZERO;
//         BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
//         int totalItems = 0;

//         for (CartItem cartItem : cart.getCartItems()) {
//             totalPrice = totalPrice.add(cartItem.getProduct().getMrpPrice());
//             totalDiscountedPrice = totalDiscountedPrice.add(cartItem.getSellingPrice());
//             totalItems += cartItem.getQuantity();
//         }

//         cart.setTotalMrpPrice(totalPrice.doubleValue());
//         cart.setTotalSellingPrice(totalDiscountedPrice.doubleValue());
//         cart.setDiscount(calculateDiscountPercent(totalPrice, totalDiscountedPrice));
//         cart.setTotalItem(totalItems);

//         cartRepository.save(cart);
//     }

//     private double calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
//         if (mrpPrice.compareTo(BigDecimal.ZERO) == 0) {
//             return 0.0;
//         }
//         return mrpPrice.subtract(sellingPrice)
//                 .divide(mrpPrice, 2, BigDecimal.ROUND_HALF_UP)
//                 .multiply(BigDecimal.valueOf(100))
//                 .doubleValue();
//     }
// }











// package com.ecommerce.ecommerce.service.impl;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.CartItem;
// import com.ecommerce.ecommerce.model.Product;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.repository.CartItemRepository;
// import com.ecommerce.ecommerce.repository.CartRepository;
// import com.ecommerce.ecommerce.service.CartService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CartServiceImpl implements CartService {
//     private final CartRepository cartRepository;
//     private final CartItemRepository cartItemRepository;

//     @Override
//     @Transactional
//     public CartItem addCartItem(User user, Product product, String size, int quantity) {
//         Cart cart = cartRepository.findByUserId(user.getId());
//         if (cart == null) {
//             cart = new Cart();
//             cart.setUser(user);
//             cart.setTotalItem(0);
//             cart.setTotalMrpPrice(BigDecimal.ZERO);
//             cart.setTotalSellingPrice(BigDecimal.ZERO);
//             cart.setDiscount(BigDecimal.ZERO);
//             cartRepository.save(cart);
//         }

//         Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

//         CartItem cartItem;
//         if (existingCartItem.isPresent()) {
//             cartItem = existingCartItem.get();
//             cartItem.setQuantity(cartItem.getQuantity() + quantity);
//         } else {
//             cartItem = new CartItem();
//             cartItem.setProduct(product);
//             cartItem.setCart(cart);
//             cartItem.setSize(size);
//             cartItem.setQuantity(quantity);
//             cartItem = cartItemRepository.save(cartItem);
//             cart.getCartItems().add(cartItem);
//         }

//         cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
//         updateCartTotals(cart);
//         return cartItem;
//     }

//     @Override
//     @Transactional
//     public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
//         CartItem cartItem = cartItemRepository.findById(cartItemId)
//                 .orElseThrow(() -> new RuntimeException("CartItem not found"));

//         if (quantity <= 0) {
//             deleteCartItem(cartItemId);
//             return null;
//         }

//         cartItem.setQuantity(quantity);
//         cartItem.setSellingPrice(cartItem.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(quantity)));

//         cartItem = cartItemRepository.save(cartItem);
//         updateCartTotals(cartItem.getCart());
//         return cartItem;
//     }

//     @Override
//     @Transactional
//     public void deleteCartItem(Long cartItemId) {
//         CartItem cartItem = cartItemRepository.findById(cartItemId)
//                 .orElseThrow(() -> new RuntimeException("CartItem not found"));

//         Cart cart = cartItem.getCart();
//         cart.getCartItems().remove(cartItem);
//         cartItemRepository.delete(cartItem);

//         updateCartTotals(cart);
//         if (cart.getCartItems().isEmpty()) {
//             cartRepository.delete(cart);
//         }
//     }

//     @Override
//     @Transactional
//     public void clearCart(User user) {
//         Cart cart = cartRepository.findByUserId(user.getId());
//         if (cart != null) {
//             cartItemRepository.deleteAll(cart.getCartItems());
//             cartRepository.delete(cart);
//         }
//     }

//     @Override
//     public Cart findUsercart(User user) {
//         return Optional.ofNullable(cartRepository.findByUserId(user.getId()))
//                 .map(cart -> {
//                     updateCartTotals(cart);
//                     return cart;
//                 }).orElse(new Cart());
//     }

//     private void updateCartTotals(Cart cart) {
//         BigDecimal totalPrice = BigDecimal.ZERO;
//         BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
//         int totalItems = 0;

//         for (CartItem cartItem : cart.getCartItems()) {
//             Product product = cartItem.getProduct();
//             if (product != null) {
//                 totalPrice = totalPrice.add(product.getMrpPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
//                 totalDiscountedPrice = totalDiscountedPrice.add(cartItem.getSellingPrice());
//                 totalItems += cartItem.getQuantity();
//             }
//         }

//         cart.setTotalMrpPrice(totalPrice);
//         cart.setTotalSellingPrice(totalDiscountedPrice);
//         cart.setDiscount(calculateDiscountPercent(totalPrice, totalDiscountedPrice));
//         cart.setTotalItem(totalItems);
//         cartRepository.save(cart);
//     }

//     private BigDecimal calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
//         if (mrpPrice.compareTo(BigDecimal.ZERO) == 0) {
//             return BigDecimal.ZERO;
//         }
//         return mrpPrice.subtract(sellingPrice)
//                 .divide(mrpPrice, 2, RoundingMode.HALF_UP)
//                 .multiply(BigDecimal.valueOf(100));
//     }
// }
