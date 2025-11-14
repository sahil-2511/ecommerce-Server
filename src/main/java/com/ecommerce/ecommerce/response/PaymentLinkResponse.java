package com.ecommerce.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentLinkResponse {
    private String payment_link_url;
    private String payment_link_id;
}
