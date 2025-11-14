package com.ecommerce.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.domain.USER_ROLE;
import com.ecommerce.ecommerce.model.VerificationCode;
import com.ecommerce.ecommerce.request.LoginOtpRequest;
import com.ecommerce.ecommerce.request.LoginRequest;
import com.ecommerce.ecommerce.request.SignupRequest;
import com.ecommerce.ecommerce.response.ApiResoponse;
import com.ecommerce.ecommerce.response.AuthResponse;
import com.ecommerce.ecommerce.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler( @RequestBody SignupRequest req) throws Exception {
        String jwt = authService.createUser(req);

        // Create and return the response with the token
        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("User registered successfully");
        res.setRole(USER_ROLE.CUSTOMER);
       

        return ResponseEntity.ok(res);
    }
        @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResoponse> sendOtpHandler(@RequestBody LoginOtpRequest  req) throws Exception {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResoponse("Email is required"));
        }
    
        authService.sentLoginOtp(req.getEmail(),req.getRole());
    
        ApiResoponse res = new ApiResoponse();
        res.setMessage("OTP sent successfully to " + req.getEmail());
    
        return ResponseEntity.ok(res);
    }
    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws Exception {
      
    AuthResponse authResponse=authService.signing(req);
    
    
       
        return ResponseEntity.ok(authResponse);
    }
    

}
