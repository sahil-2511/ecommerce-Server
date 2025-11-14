// package com.ecommerce.ecommerce.controller;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.ecommerce.ecommerce.exception.ProductException;
// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.CartItem;
// import com.ecommerce.ecommerce.model.Product;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.request.AddItemRequest;
// import com.ecommerce.ecommerce.response.ApiResoponse;
// import com.ecommerce.ecommerce.service.CartItemService;
// import com.ecommerce.ecommerce.service.CartService;
// import com.ecommerce.ecommerce.service.ProductService;
// import com.ecommerce.ecommerce.service.UserService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/cart")
// @RequiredArgsConstructor
// public class CartController {

//     private final CartService cartService;
//     private final CartItemService cartItemService;
//     private final UserService userService;
//     private final ProductService productService;

//     @GetMapping()
//     public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws Exception {
//         User user = userService.findUserByToken(jwt);
//         Cart cart = cartService.findUsercart(user);
//         return ResponseEntity.ok(cart);
//     }

//     @PutMapping("/add")
//     public ResponseEntity<CartItem> addItemToCart(
//             @RequestBody AddItemRequest req,
//             @RequestHeader("Authorization") String jwt) throws ProductException, Exception {

//         User user = userService.findUserByToken(jwt);
//         Product product = productService.findProductById(req.getProductId());

//         CartItem item = cartService.addCartItem(user, product, req.getSize(), req.getQuantity());

//         ApiResoponse res = new ApiResoponse();
//         res.setMessage("Item Added");

//         return new ResponseEntity<>( item, HttpStatus.ACCEPTED);
//     }

//     @DeleteMapping("/item/{CartItemId}")
//     public ResponseEntity<ApiResoponse>deleteCartItemHandler(@PathVariable Long cartItemId,@RequestHeader("Authorization")String jwt) throws Exception{
//         User user=userService.findUserByToken(jwt);
//         cartItemService.removeCartItem(user.getId(), cartItemId);
//         ApiResoponse res = new ApiResoponse();
//         res.setMessage("item remove from cart");

        
//         return new ResponseEntity<>(res,HttpStatus.ACCEPTED);

//     }

//     @PutMapping("/item/{cartItemId}")
//     public ResponseEntity<CartItem> updateCartItemHandler(
//             @PathVariable Long cartItemId,
//             @RequestBody CartItem cartItem,
//             @RequestHeader("Authorization") String jwt) throws Exception {
    
//         // Extract user from JWT
//         User user = userService.findUserByToken(jwt);
//      CartItem updatedCartItem= null;
//         // Check and update the cart item
//         if (cartItem.getQuantity() > 0) {
//             updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
//             return new ResponseEntity<>(updatedCartItem, HttpStatus.ACCEPTED);
//         }
    
//         // Return bad request if quantity is not valid
//         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//     }

// }



package com.ecommerce.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.ecommerce.exception.ProductException;
import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.CartItem;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.request.AddItemRequest;
import com.ecommerce.ecommerce.response.ApiResoponse;
import com.ecommerce.ecommerce.service.CartItemService;
import com.ecommerce.ecommerce.service.CartService;
import com.ecommerce.ecommerce.service.ProductService;
import com.ecommerce.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByToken(jwt);
        Cart cart = cartService.findUsercart(user);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/add")
    public ResponseEntity<ApiResoponse> addItemToCart(
            @RequestBody AddItemRequest req,
            @RequestHeader("Authorization") String jwt) throws ProductException, Exception {

        User user = userService.findUserByToken(jwt);
        Product product = productService.findProductById(req.getProductId());

        cartService.addCartItem(user, product, req.getSize(), req.getQuantity());

        ApiResoponse response = new ApiResoponse();
        response.setMessage("Item Added");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResoponse> deleteCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByToken(jwt);
        cartItemService.removeCartItem(user.getId(), cartItemId);

        ApiResoponse response = new ApiResoponse();
        response.setMessage("Item removed");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResoponse> updateCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByToken(jwt);

        if (cartItem.getQuantity() > 0) {
            cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
        }

        ApiResoponse response = new ApiResoponse();
        response.setMessage("Cart item updated");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
