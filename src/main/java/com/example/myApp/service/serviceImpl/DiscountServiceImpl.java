package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.DiscountRequest;
import com.example.myApp.enity.Discount;
import com.example.myApp.repository.DiscountRepository;
import com.example.myApp.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    @Override
    public List<Discount> getAllDiscount(){
        return discountRepository.findAll();
    }

    @Override
    public void addDiscount(DiscountRequest discountRequest){
        if (discountRequest.getCode() == null || discountRequest.getCode().trim().isEmpty()) {
            throw new RuntimeException("Mã giảm giá không được để trống!");
        }
        if(discountRepository.existsByCode(discountRequest.getCode())){
            throw new RuntimeException("Code already exists");
        }
        Discount discount = Discount.builder()
                .code(discountRequest.getCode().trim())
                .description(discountRequest.getDescription())
                .discountPercentage(discountRequest.getDiscountPercentage())
                .startDate(discountRequest.getStartDate())
                .endDate(discountRequest.getEndDate())
                .build();
        discountRepository.save(discount);
    }
}
