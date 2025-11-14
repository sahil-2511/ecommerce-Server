package com.ecommerce.ecommerce.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            mimeMessageHelper.setTo(userEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text + "\nYour OTP is: " + otp, true); // ✅ Added email body

            javaMailSender.send(mimeMessage);
            log.info("✅ OTP email sent successfully to {}", userEmail);

        } catch (MessagingException | MailException e) {
            log.error("❌ Failed to send email to {}: {}", userEmail, e.getMessage());
            throw new MailSendException("Failed to send email to " + userEmail, e);
        }
    }
}
