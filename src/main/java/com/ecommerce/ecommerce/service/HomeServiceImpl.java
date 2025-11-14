package com.ecommerce.ecommerce.service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ecommerce.ecommerce.domain.HomeCategorySection;
import com.ecommerce.ecommerce.model.Deal;
import com.ecommerce.ecommerce.model.Home;
import com.ecommerce.ecommerce.model.HomeCategory;
import com.ecommerce.ecommerce.repository.DealRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class HomeServiceImpl  implements HomeService {
    private final DealRepository dealRepository;
    @Override
    public Home createHomepageData(List<HomeCategory> allcategories) {
                List <HomeCategory> gridCategories= allcategories.stream()
                .filter(category -> category.getSection()==HomeCategorySection.GRID)
                .collect(Collectors.toList());
                List <HomeCategory> shopByCategories= allcategories.stream()
                .filter(category -> category.getSection()==HomeCategorySection.SHOP_BY_CATEGORIES)
                .collect(Collectors.toList());
                List <HomeCategory> electricCategories= allcategories.stream()  
                .filter(category -> category.getSection()==HomeCategorySection.ELECTRIC_CATEGORIES)
                .collect(Collectors.toList());
                List <HomeCategory> dealCategories= allcategories.stream()
                .filter(category -> category.getSection()==HomeCategorySection.DEALS)
                .collect(Collectors.toList());
                List<Deal> createDeals= new ArrayList<>();
                if(dealRepository.findAll().isEmpty()){
                    List <Deal> deal=allcategories.stream()
                    .filter(category -> category.getSection()==HomeCategorySection.DEALS)
                    .map(category -> new Deal(null,10,category ))
                    .collect(Collectors.toList());
                    createDeals= dealRepository.saveAll(deal);  
                }
                else{
                    createDeals= dealRepository.findAll();
                }
                Home home= new Home();
                home.setGrid(gridCategories);
                home.setShopByCategories(shopByCategories);
                home.setElectricCategories(electricCategories);
                home.setDeals(createDeals);
                home.setDealCategories(dealCategories);
                return home;
            }
}
