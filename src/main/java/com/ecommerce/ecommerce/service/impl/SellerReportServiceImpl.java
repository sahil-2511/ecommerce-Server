package com.ecommerce.ecommerce.service.impl;

import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Seller;
import com.ecommerce.ecommerce.model.SellerReport;
import com.ecommerce.ecommerce.repository.SellerReportRepository;
import com.ecommerce.ecommerce.service.SellerReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {
    
    private final SellerReportRepository sellerReportRepository;

    @Override
    public SellerReport getSellerReport(Seller seller) {
        return sellerReportRepository.findBySellerId(seller.getId())
                .orElseGet(() -> {  // If not found, create a new report
                    SellerReport newReport = new SellerReport();
                    newReport.setSeller(seller);
                    return sellerReportRepository.save(newReport);
                });
    }

    @Override
    public SellerReport updateSellerReport(SellerReport sellerReport) {
        return sellerReportRepository.save(sellerReport);
    }
}
