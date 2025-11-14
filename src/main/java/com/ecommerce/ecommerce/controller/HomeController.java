package com.ecommerce.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.response.ApiResoponse;

@RestController
public class HomeController {
@GetMapping
    public ApiResoponse  HomeControllerHandler(){
        ApiResoponse apiResoponse = new ApiResoponse();
        apiResoponse.setMessage("welcome to ecommerce site ");
        return apiResoponse;
    }


    
}
