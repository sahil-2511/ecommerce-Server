package com.ecommerce.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.model.Deal;
import com.ecommerce.ecommerce.model.HomeCategory;
import com.ecommerce.ecommerce.repository.DealRepository;
import com.ecommerce.ecommerce.repository.HomeCategoryRepository;
import com.ecommerce.ecommerce.service.DealService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final HomeCategoryRepository homeCategoryRepository;

    @Override
    public Deal createDeal(Deal deal) {
        if (deal.getCategory() == null || deal.getCategory().getId() == null) {
            throw new IllegalArgumentException("❌ Category ID must not be null");
        }

        HomeCategory homeCategory = homeCategoryRepository.findById(deal.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("❌ Category not found with id: " + deal.getCategory().getId()));

        Deal newDeal = new Deal();
        newDeal.setCategory(homeCategory);
        newDeal.setDiscount(deal.getDiscount());

        return dealRepository.save(newDeal);
    }

    @Override
    public List<Deal> getDeals() {
        return dealRepository.findAll();
    }

    @Override
    public Deal updateDeal(Deal deal, Long id) {
        Deal existingDeal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Deal not found with id: " + id));

        if (deal.getCategory() == null || deal.getCategory().getId() == null) {
            throw new IllegalArgumentException("❌ Category ID must not be null");
        }

        HomeCategory homeCategory = homeCategoryRepository.findById(deal.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("❌ Category not found with id: " + deal.getCategory().getId()));

        existingDeal.setCategory(homeCategory);

        if (deal.getDiscount() != null) {
            existingDeal.setDiscount(deal.getDiscount());
        }

        return dealRepository.save(existingDeal);
    }

    @Override
    public void deleteDeal(Long id) {
        Deal existingDeal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Deal not found with id: " + id));
        dealRepository.delete(existingDeal);
    }
}
