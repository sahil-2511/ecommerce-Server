package com.ecommerce.ecommerce.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.SellerRepository;
import com.ecommerce.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private static final String SELLER_PREFIX = "seller_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        if (username.startsWith(SELLER_PREFIX)) {
            // Handle seller authentication
            String sellerEmail = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepository.findByEmail(sellerEmail);
            
            if (seller == null) {
                throw new UsernameNotFoundException("Seller not found with email: " + sellerEmail);
            }

            return org.springframework.security.core.userdetails.User.withUsername(seller.getEmail())
                    .password(seller.getPassword())
                    .roles("SELLER")
                    .build();
        } else {
            // Handle customer authentication
            User user = userRepository.findByEmail(username);
            
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + username);
            }

            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name()) // If Role is an ENUM, use `.name()`
                    .build();
        }
    }
}

