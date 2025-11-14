// package com.ecommerce.ecommerce.request;

// import javax.crypto.SecretKey;

// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import lombok.Data;
// import lombok.Getter;
// import lombok.Setter;

// @Data
// @Getter
// @Setter
// public class SignupRequest {
//     private String email;
//     private String fullname ;
//     private String otp;
//     public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
//             Decoders.BASE64.decode("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF")
//     );

// }


package com.ecommerce.ecommerce.request;

import javax.crypto.SecretKey;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {
    private String email;
    private String fullname ;
    private String otp;
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF")
    );

}
