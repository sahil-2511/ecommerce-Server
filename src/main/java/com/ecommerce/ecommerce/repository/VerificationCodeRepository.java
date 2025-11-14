package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecommerce.model.VerificationCode;


@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    VerificationCode findByEmail(String email); // Use Optional to avoid NullPointerException
    VerificationCode findByOtp(String otp);
    void delete(VerificationCode verificationCode); // No need to explicitly declare, JpaRepository provides this.
}
