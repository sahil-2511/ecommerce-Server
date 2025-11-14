package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.model.User;

public interface UserService {
     User findUserByToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
}
