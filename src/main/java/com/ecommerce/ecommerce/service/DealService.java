package com.ecommerce.ecommerce.service;

import java.util.List;

import com.ecommerce.ecommerce.model.Deal;

public interface DealService {
    Deal createDeal(Deal deal);

    List<Deal> getDeals();
    Deal updateDeal( Deal deal ,Long id);
    void deleteDeal(Long id);

}