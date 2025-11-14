// package com.ecommerce.ecommerce.repository;

// import com.ecommerce.ecommerce.model.Category;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface CategoryRepository extends JpaRepository<Category, Long> {
//     Category findByCategoryId(String categoryId);
// }
package com.ecommerce.ecommerce.repository;

import com.ecommerce.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(String categoryId); // Use Optional to handle null cases
}