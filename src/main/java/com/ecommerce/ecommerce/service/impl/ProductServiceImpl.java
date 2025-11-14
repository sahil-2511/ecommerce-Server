// package com.ecommerce.ecommerce.service.impl;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.stereotype.Service;

// import com.ecommerce.ecommerce.exception.ProductException;
// import com.ecommerce.ecommerce.model.Category;
// import com.ecommerce.ecommerce.model.Product;
// import com.ecommerce.ecommerce.model.Seller;
// import com.ecommerce.ecommerce.repository.CategoryRepository;
// import com.ecommerce.ecommerce.repository.ProductRepository;
// import com.ecommerce.ecommerce.request.CreateProductRequest;
// import com.ecommerce.ecommerce.service.ProductService;

// import jakarta.persistence.criteria.Join;
// import jakarta.persistence.criteria.Predicate;

// @Service
// public class ProductServiceImpl implements ProductService {

//     private final ProductRepository productRepository;
//     private final CategoryRepository categoryRepository;

//     @Autowired
//     public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
//         this.productRepository = productRepository;
//         this.categoryRepository = categoryRepository;
//     }

//     @Override
//     public Product createProduct(CreateProductRequest req, Seller seller) {
//         // Find or create Category
//         Category category = categoryRepository.findByCategoryId(req.getCategory());
//         if (category == null) {
//             category = new Category();
//             category.setCategoryId(req.getCategory());
//             category = categoryRepository.save(category);
//         }

//         // Create Product
//         Product product = new Product();
//         product.setSeller(seller);
//         product.setCategory(category);
//         product.setTitle(req.getTitle());
//         product.setDescription(req.getDescription());
//         product.setColor(req.getColor());
//         product.setCreatedAt(LocalDateTime.now());
//         product.setMrpPrice(req.getMrpPrice());
//         product.setSellingPrice(req.getSellingPrice());
//         product.setDiscountPercent(calculateDiscountPercent(req.getMrpPrice(), req.getSellingPrice()));
//         product.setImages(req.getImages());
//         product.setSizes(req.getSizes());

//         return productRepository.save(product);
//     }

//     private double calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
//         if (mrpPrice == null || sellingPrice == null || mrpPrice.compareTo(BigDecimal.ZERO) == 0) {
//             return 0.0;
//         }
//         return ((mrpPrice.subtract(sellingPrice))
//                 .divide(mrpPrice, 2, BigDecimal.ROUND_HALF_UP))
//                 .multiply(BigDecimal.valueOf(100))
//                 .doubleValue();
//     }

//     @Override
//     public void deleteProduct(Long productId) {
//         productRepository.deleteById(productId);
//     }

//     @Override
//     public Product updateProduct(Long productId, Product product) throws ProductException {
//         return productRepository.findById(productId)
//                 .map(existingProduct -> {
//                     if (product.getTitle() != null) existingProduct.setTitle(product.getTitle());
//                     if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
//                     if (product.getMrpPrice() != null) existingProduct.setMrpPrice(product.getMrpPrice());
//                     if (product.getSellingPrice() != null) existingProduct.setSellingPrice(product.getSellingPrice());
//                     if (product.getColor() != null) existingProduct.setColor(product.getColor());
//                     if (product.getImages() != null) existingProduct.setImages(product.getImages());
//                     if (product.getSizes() != null) existingProduct.setSizes(product.getSizes());

//                     return productRepository.save(existingProduct);
//                 })
//                 .orElseThrow(() -> new ProductException("Product not found with ID: " + productId));
//     }

//     @Override
//     public Product findProductById(Long productId) throws ProductException {
//         return productRepository.findById(productId)
//                 .orElseThrow(() -> new ProductException("Product not found with ID: " + productId));
//     }

//     @Override
//     public List<Product> searchProducts(String query, Pageable pageable) {
//         return productRepository.searchProduct(query, pageable);
//     }

//     @Override
//     public Page<Product> getAllProducts(
//             String category,
//             String description,
//             String brand,
//             String color,
//             List<String> sizes,
//             Integer minPrice,
//             Integer maxPrice,
//             Integer minDiscount,
//             String sort,
//             String stock,
//             Integer pageNumber) {

//         Specification<Product> spec = (root, query, criteriaBuilder) -> {
//             List<Predicate> predicates = new ArrayList<>();

//             if (category != null && !category.isEmpty()) {
//                 Join<Product, Category> categoryJoin = root.join("category");
//                 predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), category));
//             }

//             if (color != null && !color.isEmpty()) {
//                 predicates.add(criteriaBuilder.equal(root.get("color"), color));
//             }

//             if (sizes != null && !sizes.isEmpty()) {
//                 predicates.add(root.get("sizes").in(sizes));
//             }

//             if (minPrice != null) {
//                 predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), BigDecimal.valueOf(minPrice)));
//             }
//             if (maxPrice != null) {
//                 predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), BigDecimal.valueOf(maxPrice)));
//             }

//             if (description != null && !description.isEmpty()) {
//                 predicates.add(criteriaBuilder.like(root.get("description"), "%" + description + "%"));
//             }

//             if (minDiscount != null) {
//                 predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount));
//             }

//             if (stock != null) {
//                 boolean isAvailable = stock.equalsIgnoreCase("in_stock");
//                 predicates.add(criteriaBuilder.equal(root.get("isAvailable"), isAvailable));
//             }

//             return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//         };

//         Pageable sortedPageable = createSortedPageable(sort, pageNumber);
//         return productRepository.findAll(spec, sortedPageable);
//     }

//     private Pageable createSortedPageable(String sort, Integer pageNumber) {
//         Sort sorting = switch (sort) {
//             case "price_low" -> Sort.by("sellingPrice").ascending();
//             case "price_high" -> Sort.by("sellingPrice").descending();
//             case "latest" -> Sort.by("createdAt").descending();
//             default -> Sort.unsorted();
//         };
        
//         return PageRequest.of(pageNumber, 10, sorting);  // Default page size = 10
//     }

//     @Override
//     public Page<Product> getProductBySeller_Id(Long sellerId, Pageable pageable) {
//         return productRepository.findBySeller_Id(sellerId, pageable);
//     }

  
// }






package com.ecommerce.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.exception.ProductException;
import com.ecommerce.ecommerce.model.Category;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.repository.CategoryRepository;
import com.ecommerce.ecommerce.repository.ProductRepository;
import com.ecommerce.ecommerce.request.CreateProductRequest;
import com.ecommerce.ecommerce.service.ProductService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }



    @Override
public Product createProduct(CreateProductRequest req, Seller seller) {
    // Find or create Category
    Optional<Category> categoryOpt = categoryRepository.findByCategoryId(req.getCategory());
    Category category = categoryOpt.orElseGet(() -> {
        Category newCategory = new Category();
        newCategory.setCategoryId(req.getCategory());
        newCategory.setName(req.getCategory()); // Assuming name is the same as categoryId
        return categoryRepository.save(newCategory);
    });

    // Create Product
    Product product = new Product();
    product.setSeller(seller);
    product.setCategory(category);
    product.setTitle(req.getTitle());
    product.setDescription(req.getDescription());
    product.setColors(req.getColors());
    product.setCreatedAt(LocalDateTime.now());
    product.setMrpPrice(req.getMrpPrice());
    product.setSellingPrice(req.getSellingPrice());
    product.setDiscountPercent(calculateDiscountPercent(req.getMrpPrice(), req.getSellingPrice()));
    product.setImages(req.getImages());
    product.setQuantity(req.getQuantity());
    product.setSizes(req.getSizes());

    return productRepository.save(product);
}

    private double calculateDiscountPercent(BigDecimal mrpPrice, BigDecimal sellingPrice) {
        if (mrpPrice == null || sellingPrice == null || mrpPrice.compareTo(BigDecimal.ZERO) == 0 || sellingPrice.compareTo(mrpPrice) > 0) {
            return 0.0;
        }
        return ((mrpPrice.subtract(sellingPrice))
                .divide(mrpPrice, 2, BigDecimal.ROUND_HALF_UP))
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Override
    public void deleteProduct(Long productId) throws Exception {
        try {
            productRepository.deleteById(productId);
        } catch (EmptyResultDataAccessException e) {
            throw new Exception("Product not found with ID: " + productId);
        } catch (Exception e) {
            throw new Exception("Failed to delete product: " + e.getMessage());
        }
    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        return productRepository.findById(productId)
                .map(existingProduct -> {
                    if (product.getTitle() != null) existingProduct.setTitle(product.getTitle());
                    if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
                    if (product.getMrpPrice() != null) existingProduct.setMrpPrice(product.getMrpPrice());
                    if (product.getSellingPrice() != null) existingProduct.setSellingPrice(product.getSellingPrice());
                     if (product.getQuantity() != null) existingProduct.setQuantity(product.getQuantity());
                    if (product.getColors() != null) {
                        existingProduct.setColors(product.getColors());
                    }
                    
                    if (product.getImages() != null) existingProduct.setImages(product.getImages());
                    if (product.getSizes() != null) existingProduct.setSizes(product.getSizes());

                    // Recalculate discount percent if mrpPrice or sellingPrice is updated
                    if (product.getMrpPrice() != null || product.getSellingPrice() != null) {
                        existingProduct.setDiscountPercent(calculateDiscountPercent(
                                existingProduct.getMrpPrice(),
                                existingProduct.getSellingPrice()
                        ));
                    }

                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new ProductException("Product not found with ID: " + productId));
    }

    @Override
    public Product findProductById(Long productId) throws ProductException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found with ID: " + productId));
    }

    @Override
    public List<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProduct(query, pageable);
    }

    @Override
    public Page<Product> getAllProducts(
            String category,
            String description,
            String brand,
            List<String> colors,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber) {
    
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
    
            if (category != null && !category.isEmpty()) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("name"), category));
            }
    
            if (colors != null && !colors.isEmpty()) {
                List<Predicate> colorPredicates = new ArrayList<>();
                for (String color : colors) {
                    colorPredicates.add(criteriaBuilder.isMember(color, root.get("colors")));
                }
                predicates.add(criteriaBuilder.or(colorPredicates.toArray(new Predicate[0])));
            }
    
            if (sizes != null && !sizes.isEmpty()) {
                predicates.add(root.get("sizes").in(sizes));
            }
    
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), BigDecimal.valueOf(minPrice)));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), BigDecimal.valueOf(maxPrice)));
            }
    
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + description + "%"));
            }
    
            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand"), brand));
            }
    
            if (minDiscount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount));
            }
    
            if (stock != null) {
                boolean isAvailable = stock.equalsIgnoreCase("in_stock");
                predicates.add(criteriaBuilder.equal(root.get("isAvailable"), isAvailable));
            }
    
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    
        Pageable sortedPageable = createSortedPageable(sort, pageNumber);
        return productRepository.findAll(spec, sortedPageable);
    }
    


    private Pageable createSortedPageable(String sort, Integer pageNumber) {
        // ✅ Handle null case for sorting
        if (sort == null || sort.trim().isEmpty()) {
            sort = "latest"; // Default to latest products
        }

        // ✅ Ensure pageNumber is not null
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        Sort sorting = switch (sort.toLowerCase()) {
            case "price_low" -> Sort.by("sellingPrice").ascending();
            case "price_high" -> Sort.by("sellingPrice").descending();
            case "latest" -> Sort.by("createdAt").descending();
            default -> Sort.by("createdAt").descending(); // Default sorting
        };

        return PageRequest.of(pageNumber, 10, sorting); // ✅ Default page size = 10
    }


    @Override
    public Page<Product> getProductBySeller_Id(Long sellerId, Pageable pageable) {
        return productRepository.findBySeller_Id(sellerId, pageable);
    }

}