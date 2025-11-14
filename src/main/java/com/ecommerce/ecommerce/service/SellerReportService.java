package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);  // Changed String to Long
    SellerReport updateSellerReport(SellerReport sellerReport);
}
