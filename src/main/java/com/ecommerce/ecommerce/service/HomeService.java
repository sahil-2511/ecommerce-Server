package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.model.Home;
import com.ecommerce.ecommerce.model.HomeCategory;

public  interface HomeService {
    public Home createHomepageData(List<HomeCategory> allcategories);
    
  
}
