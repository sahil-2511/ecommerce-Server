package com.ecommerce.ecommerce.service;

import java.util.List;


import com.ecommerce.ecommerce.model.HomeCategory;

public interface HomeCategoryService {

    List<HomeCategory>createCategories(List<HomeCategory> homeCategories);
    HomeCategory updateCategory(HomeCategory homeCategory, Long id);
    List<HomeCategory> getAllCategories();



    
}
