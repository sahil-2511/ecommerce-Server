package com.ecommerce.ecommerce.service.impl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecommerce.ecommerce.domain.USER_ROLE;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class DatainitizationComponent implements CommandLineRunner {
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;



@Override 
public void run (String... args){
     initializeAdminUser();

}
private void initializeAdminUser() {
    String adminUsername = "testing.rahulkr@gmail.com";
    User existing = userRepository.findByEmail(adminUsername);

    if (existing == null || existing.getRole() == null) {
        if (existing != null) {
            userRepository.delete(existing); // remove corrupted one
        }

        User adminUser = new User();
        adminUser.setPassword(passwordEncoder.encode("test.rahul"));
        adminUser.setFullname("RahulKumar");
        adminUser.setEmail(adminUsername);
        adminUser.setRole(USER_ROLE.ADMIN);
        userRepository.save(adminUser);

        System.out.println("✅ Admin user initialized");
    }
}


}