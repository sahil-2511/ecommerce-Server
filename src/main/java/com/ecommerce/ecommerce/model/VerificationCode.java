package com.ecommerce.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// @EqualsAndHashCode
// public class VerificationCode {
//           @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;
//     private String otp;
//     private String email;
//     @OneToOne 
//     private User user;
//     @OneToOne
//     private Seller seller;



// }
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    private Long id;

    private String otp;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = true)
    private Seller seller;
}
