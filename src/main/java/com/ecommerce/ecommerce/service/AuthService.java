package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.domain.USER_ROLE;
import com.ecommerce.ecommerce.request.LoginRequest;
import com.ecommerce.ecommerce.request.SignupRequest;
import com.ecommerce.ecommerce.response.AuthResponse;

public interface AuthService {
    
    // Register a new user
    void sentLoginOtp(String email, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    
    // Authenticate a user and return JWT token
    AuthResponse signing(LoginRequest req);
   
}

