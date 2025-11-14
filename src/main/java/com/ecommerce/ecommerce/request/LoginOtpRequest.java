package com.ecommerce.ecommerce.request;

import com.ecommerce.ecommerce.domain.USER_ROLE;

import lombok.Data;
@Data
public class LoginOtpRequest {
     private String email;
     private String otp;
     private String password;
    private  USER_ROLE role;
}
