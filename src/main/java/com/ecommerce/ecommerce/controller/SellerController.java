package com.ecommerce.ecommerce.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.ecommerce.domain.AccountStatus;
import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.SellerReport;
import com.ecommerce.ecommerce.model.VerificationCode;
import com.ecommerce.ecommerce.repository.SellerReportRepository;
import com.ecommerce.ecommerce.repository.SellerRepository;
import com.ecommerce.ecommerce.repository.VerificationCodeRepository;
import com.ecommerce.ecommerce.request.LoginRequest;
import com.ecommerce.ecommerce.response.AuthResponse;
import com.ecommerce.ecommerce.service.AuthService;
import com.ecommerce.ecommerce.service.EmailService;
import com.ecommerce.ecommerce.service.SellerReportService;
import com.ecommerce.ecommerce.service.SellerService;
import com.ecommerce.ecommerce.utils.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {


    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private  final SellerRepository sellerRepository;
    private final SellerReportRepository sellerReportRepository;

    private final SellerReportService sellerReportService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(@RequestBody LoginRequest req) throws Exception {
    String otp =req.getOtp();
      String email=req.getEmail();
    req.setEmail("seller_"+email);
    AuthResponse authResponse=authService.signing(req);
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller>verifySellerEmail(@PathVariable String otp)throws Exception{
      VerificationCode verificationCode  = verificationCodeRepository.findByOtp(otp);
      if(verificationCode==null|| !verificationCode.getOtp().equals(otp)){
        throw new Exception("wrong otp h ");
      }
      Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);
      return new ResponseEntity<>(seller,HttpStatus.OK);
    
    }

@PostMapping("/create-seller")
public ResponseEntity<Seller> createSeller(
        @RequestBody Seller seller) throws Exception, MessagingException {
    
    // Save seller
    Seller savedSeller = sellerService.createSeller(seller);

    // Generate OTP
    String otp = OtpUtil.generateOtp();

    // Create verification code

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());


        verificationCodeRepository.save(verificationCode);


    // Email subject and message
    String subject = "Zosh Bazaar Email Verification Code";
    String frontendUrl = "http://localhost:5173/verify-seller/";
    String text = "Welcome to Zosh Bazaar, verify your account using this link: " + frontendUrl + otp;

    // Send email
    emailService.sendVerificationOtpEmail(seller.getEmail(),verificationCode.getOtp(), subject, text);

    // Return response
    return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
}

@GetMapping("/{id}")
public ResponseEntity<Seller>getSellerById(@PathVariable Long id)throws Exception{ 
  Seller seller =seller=sellerService.getSellerById(id);
  return new ResponseEntity<>(seller,HttpStatus.OK);
}
@GetMapping("/profile")
public ResponseEntity<Seller>getSellerByJwt(
  @RequestHeader("Authorization")String jwt
)throws Exception{

  Seller seller= sellerService.getSellerProfile(jwt);
  return new ResponseEntity<>(seller,HttpStatus.OK);
}


@GetMapping("/all")
public ResponseEntity<List<Seller>> getAllSellers(
    @RequestParam(required = false) AccountStatus status) { 
    List<Seller> sellers = sellerService.getAllSellers(status);
    return ResponseEntity.ok(sellers);
}
@PatchMapping()
public ResponseEntity<Seller>updateSeller(
  @RequestHeader("Authorization")String jwt,@RequestBody Seller seller)throws Exception{
    Seller profile= sellerService.getSellerProfile(jwt);
    Seller updatSeller=sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatSeller);
  }

  @GetMapping("/report")
  public ResponseEntity<SellerReport> getSellerReport(@RequestHeader("Authorization") String jwt) throws Exception {
      Seller seller = sellerService.getSellerProfile(jwt);
      SellerReport report = sellerReportService.getSellerReport(seller);
      return new ResponseEntity<>(report, HttpStatus.OK);
  }
  
}

