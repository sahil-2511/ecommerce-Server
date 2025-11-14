package com.ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.exception.ProductException;
import com.ecommerce.ecommerce.exception.SellerException;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.request.CreateProductRequest;
import com.ecommerce.ecommerce.service.ProductService;
import com.ecommerce.ecommerce.service.SellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/products")
public class SellerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SellerService sellerService;

    /**
     * ✅ Get all products for a seller with pagination
          * @throws Exception 
          */
         @GetMapping("/all")
         public ResponseEntity<List<Product>> getProductsBySeller(
                 @RequestHeader("Authorization") String jwt,
                 @RequestParam(defaultValue = "0") int page,
                 @RequestParam(defaultValue = "40") int size) throws Exception {

        // Get Seller from JWT Token
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            throw new SellerException("Seller not found or invalid JWT token.");
        }

        // Fetch paginated products by Seller ID
        Page<Product> productPage = productService.getProductBySeller_Id(seller.getId(), PageRequest.of(page, size));

        return new ResponseEntity<>(productPage.getContent(), HttpStatus.OK);
    }

    /**
     * ✅ Create a new product for the seller
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody CreateProductRequest request,
            @RequestHeader("Authorization") String jwt) throws Exception {

        // Get Seller from JWT Token
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            throw new SellerException("Seller not found or invalid JWT token.");
        }

        // Create product
        Product createdProduct = productService.createProduct(request, seller);

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * ✅ Update an existing product by Seller
          * @throws Exception 
          */
         @PutMapping("/{productId}")
         public ResponseEntity<Product> updateProduct(
                 @PathVariable Long productId,
                 @RequestBody Product product,
                 @RequestHeader("Authorization") String jwt) throws Exception {

        // Get Seller from JWT Token
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            throw new SellerException("Seller not found or invalid JWT token.");
        }

        // Find existing product and verify ownership
        Product existingProduct = productService.findProductById(productId);
        if (!existingProduct.getSeller().getId().equals(seller.getId())) {
            throw new ProductException("You are not authorized to update this product.");
        }

        // Update product
        Product updatedProduct = productService.updateProduct(productId, product);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * ✅ Delete a product by Seller
          * @throws Exception 
          */
         @DeleteMapping("/{productId}/delete")
         public ResponseEntity<String> deleteProduct(
                 @PathVariable Long productId,
                 @RequestHeader("Authorization") String jwt) throws Exception {

        // Get Seller from JWT Token
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            throw new SellerException("Seller not found or invalid JWT token.");
        }

        productService.deleteProduct(productId);

        return new ResponseEntity<>("Product deleted successfully.", HttpStatus.OK);
    }
}
