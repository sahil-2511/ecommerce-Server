package com.ecommerce.ecommerce.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.ecommerce.ecommerce.config.JwtProvider;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserByToken(String jwt) throws Exception {
      String email= jwtProvider.getEmailFromJwtToken(jwt);
      return this.findUserByEmail(email);

    }


    @Override
    public User findUserByEmail(String email) throws Exception {
User user =userRepository.findByEmail(email);
if (user== null) {
    throw new Exception("user not found with this number");
    
}


        return userRepository.findByEmail(email);
    }

   
  
}
