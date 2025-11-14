package com.ecommerce.ecommerce.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.config.JwtProvider;
import com.ecommerce.ecommerce.domain.USER_ROLE;
import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.model.VerificationCode;
import com.ecommerce.ecommerce.repository.*;
import com.ecommerce.ecommerce.request.LoginRequest;
import com.ecommerce.ecommerce.request.SignupRequest;
import com.ecommerce.ecommerce.response.AuthResponse;
import com.ecommerce.ecommerce.service.AuthService;
import com.ecommerce.ecommerce.service.EmailService;
import com.ecommerce.ecommerce.utils.OtpUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customService;

    
    @Override
public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
    String SIGNING_PREFIX = "signin_";
    if (email.startsWith(SIGNING_PREFIX)) {
        email = email.substring(SIGNING_PREFIX.length());
    }

    User user = null;
    Seller seller = null;
    boolean isSignup = false;

    if (role.equals(USER_ROLE.CUSTOMER)) {
        user = userRepository.findByEmail(email);
        if (user == null) {
            isSignup = true; // This means user is registering
        }
    } else if (role.equals(USER_ROLE.SELLER)) {
        seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            isSignup = true; // This means seller is registering
        }
    } else {
        throw new Exception("Invalid role specified.");
    }

    // ✅ Delete existing OTP if present
    VerificationCode existingCode = verificationCodeRepository.findByEmail(email);
    if (existingCode != null) {
        verificationCodeRepository.delete(existingCode);
    }

    // ✅ Generate and Save OTP
    String otp = OtpUtil.generateOtp();
    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setEmail(email);
    verificationCode.setOtp(otp);

    if (!isSignup) { // If it's not a signup, link existing user/seller
        verificationCode.setUser(user);
        verificationCode.setSeller(seller);
    }

    verificationCodeRepository.save(verificationCode);

    // ✅ Send OTP via email
    String subject = isSignup ? "My Bazaar Signup OTP" : "My Bazaar Login OTP";
    String text = "Your OTP is: " + otp;
    emailService.sendVerificationOtpEmail(email, otp, subject, text);
}


    @Override
    public String createUser(SignupRequest req) throws Exception {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new Exception("User with this email already exists.");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Wrong OTP.");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        user.setRole(USER_ROLE.CUSTOMER);
        // user.setMobile("9334398684");
        user.setPassword(passwordEncoder.encode(req.getOtp()));
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) {
        String username = req.getEmail();
        String otp = req.getOtp();
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login successful.");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        if (roleName != null && roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);
        }

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    public Authentication authenticate(String username, String otp) throws BadCredentialsException {
        UserDetails userDetails = customService.loadUserByUsername(username);
        String SELLER_PREFIX = "seller_";
        if (username.startsWith(SELLER_PREFIX)) {
            username = username.substring(SELLER_PREFIX.length());
        }

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong OTP.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
































// package com.ecommerce.ecommerce.service.impl;

// import java.time.LocalDateTime;
// import java.util.Collection;
// import java.util.List;

// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.ecommerce.ecommerce.config.JwtProvider;
// import com.ecommerce.ecommerce.domain.USER_ROLE;
// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.Seller;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.model.VerificationCode;
// import com.ecommerce.ecommerce.repository.*;
// import com.ecommerce.ecommerce.request.LoginRequest;
// import com.ecommerce.ecommerce.request.SignupRequest;
// import com.ecommerce.ecommerce.response.AuthResponse;
// import com.ecommerce.ecommerce.service.AuthService;
// import com.ecommerce.ecommerce.service.EmailService;
// import com.ecommerce.ecommerce.utils.OtpUtil;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AuthServiceImpl implements AuthService {

  

//     private final UserRepository userRepository;
//     private final SellerRepository sellerRepository;
//     private final CartRepository cartRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtProvider jwtProvider;
//     private final VerificationCodeRepository verificationCodeRepository;
//     private final EmailService emailService;
//     private final CustomUserServiceImpl customeService;
    



//     // ✅ OTP Generation and Sending
//     @Override
//     public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
//         String SIGNING_PREFIX = "signin_";
//         if (email.startsWith(SIGNING_PREFIX)) {
//             email = email.substring(SIGNING_PREFIX.length());
//         }
    
//         VerificationCode verificationCode = new VerificationCode();
//         verificationCode.setEmail(email);
    
//         // ✅ Check if user exists based on role
//         if (role.equals(USER_ROLE.CUSTOMER)) {
//             User user = userRepository.findByEmail(email);
//             if (user != null) {
//                 throw new Exception("Customer with this email does not exist.");
//             }
//             verificationCode.setUser(user);  // ✅ Link user to OTP
//         } else if (role.equals(USER_ROLE.SELLER)) {
//             Seller seller = sellerRepository.findByEmail(email);
//             if (seller == null) {
//                 throw new Exception("Seller with this email does not exist.");
//             }
//             verificationCode.setSeller(seller);  // ✅ Link seller to OTP
//         } else {
//             throw new Exception("Invalid role specified.");
//         }
    
//         // ✅ Delete existing OTP if present
//         VerificationCode existingCode = verificationCodeRepository.findByEmail(email);
//         if (existingCode != null) {
//             verificationCodeRepository.delete(existingCode);
//         }
    
//         // ✅ Generate and Save OTP
//         String otp = OtpUtil.generateOtp();
//         verificationCode.setOtp(otp);
//         verificationCodeRepository.save(verificationCode);
    
//         // ✅ Send OTP via email
//         String subject = "My Bazaar Login/Signup OTP";
//         String text = "Your login/signup OTP is: " + otp + " (Valid for 5 minutes)";
//         emailService.sendVerificationOtpEmail(email, otp, subject, text);
//     }
    
//     // ✅ Create New User After OTP Verification
//     @Override
//     public String createUser(SignupRequest req) throws Exception {
//         // ✅ Check if a user already exists with the given email
//         if (userRepository.existsByEmail(req.getEmail())) {
//             throw new Exception("User with this email already exists.");
//         }

//         // ✅ Fetch OTP from repository
//         VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

//         // ✅ Validate OTP
//         if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
//             throw new Exception("Wrong OTP.");
//         }

//         // ✅ Create a new user if OTP is valid
//         User user = new User();
//         user.setEmail(req.getEmail());
//         user.setFullname(req.getFullname());
//         user.setRole(USER_ROLE.CUSTOMER); // Default role: CUSTOMER
//         user.setMobile("9334398684");
//         user.setPassword(passwordEncoder.encode(req.getOtp())); // Encrypt password
//         user = userRepository.save(user);

//         // ✅ Create Cart for New User
//         Cart cart = new Cart();
//         cart.setUser(user);
//         cartRepository.save(cart);

//         // ✅ Grant Authority & Authenticate User
//         List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//         Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
//         SecurityContextHolder.getContext().setAuthentication(authentication);

//         // ✅ Generate JWT Token
//         return jwtProvider.generateToken(authentication);
//     }





//     // ✅ Login User Using OTP
//     @Override
//     public AuthResponse signing(LoginRequest req) {
//         String username = req.getEmail();
//         String otp = req.getOtp();
//         Authentication authentication = authenticate(username, otp);
//         SecurityContextHolder.getContext().setAuthentication(authentication);

//         // ✅ Generate JWT Token
//         String token = jwtProvider.generateToken(authentication);
//         AuthResponse authResponse = new AuthResponse();
//         authResponse.setJwt(token);
//         authResponse.setMessage("Login successful.");

//         // ✅ Extract Role & Remove "ROLE_" Prefix
//         Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//         String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
//         if (roleName != null && roleName.startsWith("ROLE_")) {
//             roleName = roleName.substring(5); // ✅ Remove "ROLE_" prefix
//         }

//         authResponse.setRole(USER_ROLE.valueOf(roleName)); // ✅ Now it correctly maps to CUSTOMER, SELLER, etc.
//         return authResponse;
//     }

//     // ✅ Authenticate User with OTP
//     public Authentication authenticate(String username, String otp) throws BadCredentialsException {
//         // ✅ Load user details
//         UserDetails userDetails = customeService.loadUserByUsername(username);
// String SELLER_PREFIX="seller_";
// if(username.startsWith(SELLER_PREFIX)){
//     username=username.substring(SELLER_PREFIX.length());
// }




//         if (userDetails == null) {
//             throw new BadCredentialsException("Invalid username or password.");
//         }

//         // ✅ Fetch OTP from database
//         VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
//         if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
//             throw new BadCredentialsException("Wrong OTP.");
//         }

//         // ✅ Return Authentication Token
//         return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//     }
// }






































// package com.ecommerce.ecommerce.service.impl;

// import java.time.LocalDateTime;
// import java.util.Collection;
// import java.util.List;

// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.ecommerce.ecommerce.config.JwtProvider;
// import com.ecommerce.ecommerce.domain.USER_ROLE;
// import com.ecommerce.ecommerce.model.Cart;
// import com.ecommerce.ecommerce.model.Seller;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.model.VerificationCode;
// import com.ecommerce.ecommerce.repository.*;
// import com.ecommerce.ecommerce.request.LoginRequest;
// import com.ecommerce.ecommerce.request.SignupRequest;
// import com.ecommerce.ecommerce.response.AuthResponse;
// import com.ecommerce.ecommerce.service.AuthService;
// import com.ecommerce.ecommerce.service.EmailService;
// import com.ecommerce.ecommerce.utils.OtpUtil;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AuthServiceImpl implements AuthService {

  

//     private final UserRepository userRepository;
//     private final SellerRepository sellerRepository;
//     private final CartRepository cartRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtProvider jwtProvider;
//     private final VerificationCodeRepository verificationCodeRepository;
//     private final EmailService emailService;
//     private final CustomUserServiceImpl customeService;
    



//     // ✅ OTP Generation and Sending
//     @Override
//     public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
//         String SIGNING_PREFIX = "signin_";
//         if (email.startsWith(SIGNING_PREFIX)) {
//             email = email.substring(SIGNING_PREFIX.length());
//         }
    
//         VerificationCode verificationCode = new VerificationCode();
//         verificationCode.setEmail(email);
    
//         // ✅ Check if user exists based on role
//         if (role.equals(USER_ROLE.CUSTOMER)) {
//             User user = userRepository.findByEmail(email);
//             if (user != null) {
//                 throw new Exception("Customer with this email does not exist.");
//             }
//             verificationCode.setUser(user);  // ✅ Link user to OTP
//         } else if (role.equals(USER_ROLE.SELLER)) {
//             Seller seller = sellerRepository.findByEmail(email);
//             if (seller == null) {
//                 throw new Exception("Seller with this email does not exist.");
//             }
//             verificationCode.setSeller(seller);  // ✅ Link seller to OTP
//         } else {
//             throw new Exception("Invalid role specified.");
//         }
    
//         // ✅ Delete existing OTP if present
//         VerificationCode existingCode = verificationCodeRepository.findByEmail(email);
//         if (existingCode != null) {
//             verificationCodeRepository.delete(existingCode);
//         }
    
//         // ✅ Generate and Save OTP
//         String otp = OtpUtil.generateOtp();
//         verificationCode.setOtp(otp);
//         verificationCodeRepository.save(verificationCode);
    
//         // ✅ Send OTP via email
//         String subject = "My Bazaar Login/Signup OTP";
//         String text = "Your login/signup OTP is: " + otp + " (Valid for 5 minutes)";
//         emailService.sendVerificationOtpEmail(email, otp, subject, text);
//     }
    
//     // ✅ Create New User After OTP Verification
//     @Override
//     public String createUser(SignupRequest req) throws Exception {
//         // ✅ Check if a user already exists with the given email
//         if (userRepository.existsByEmail(req.getEmail())) {
//             throw new Exception("User with this email already exists.");
//         }

//         // ✅ Fetch OTP from repository
//         VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

//         // ✅ Validate OTP
//         if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
//             throw new Exception("Wrong OTP.");
//         }

//         // ✅ Create a new user if OTP is valid
//         User user = new User();
//         user.setEmail(req.getEmail());
//         user.setFullname(req.getFullname());
//         user.setRole(USER_ROLE.CUSTOMER); // Default role: CUSTOMER
//         user.setMobile("9334398684");
//         user.setPassword(passwordEncoder.encode(req.getOtp())); // Encrypt password
//         user = userRepository.save(user);

//         // ✅ Create Cart for New User
//         Cart cart = new Cart();
//         cart.setUser(user);
//         cartRepository.save(cart);

//         // ✅ Grant Authority & Authenticate User
//         List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//         Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
//         SecurityContextHolder.getContext().setAuthentication(authentication);

//         // ✅ Generate JWT Token
//         return jwtProvider.generateToken(authentication);
//     }





//     // ✅ Login User Using OTP
//     @Override
//     public AuthResponse signing(LoginRequest req) {
//         String username = req.getEmail();
//         String otp = req.getOtp();
//         Authentication authentication = authenticate(username, otp);
//         SecurityContextHolder.getContext().setAuthentication(authentication);

//         // ✅ Generate JWT Token
//         String token = jwtProvider.generateToken(authentication);
//         AuthResponse authResponse = new AuthResponse();
//         authResponse.setJwt(token);
//         authResponse.setMessage("Login successful.");

//         // ✅ Extract Role & Remove "ROLE_" Prefix
//         Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//         String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
//         if (roleName != null && roleName.startsWith("ROLE_")) {
//             roleName = roleName.substring(5); // ✅ Remove "ROLE_" prefix
//         }

//         authResponse.setRole(USER_ROLE.valueOf(roleName)); // ✅ Now it correctly maps to CUSTOMER, SELLER, etc.
//         return authResponse;
//     }

//     // ✅ Authenticate User with OTP
//     public Authentication authenticate(String username, String otp) throws BadCredentialsException {
//         // ✅ Load user details
//         UserDetails userDetails = customeService.loadUserByUsername(username);
// String SELLER_PREFIX="seller_";
// if(username.startsWith(SELLER_PREFIX)){
//     username=username.substring(SELLER_PREFIX.length());
// }




//         if (userDetails == null) {
//             throw new BadCredentialsException("Invalid username or password.");
//         }

//         // ✅ Fetch OTP from database
//         VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
//         if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
//             throw new BadCredentialsException("Wrong OTP.");
//         }

//         // ✅ Return Authentication Token
//         return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//     }
// }
