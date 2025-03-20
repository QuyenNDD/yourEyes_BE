package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Revenues;
import com.example.myApp.repository.RevenueRepository;
import com.example.myApp.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final RevenueRepository revenueRepository;

    @Override
    public List<Revenues> getRevenueByMonthAndYear(int month, int year){
        return revenueRepository.findByRevenueMonthAndRevenueYear(month, year);
    }

    @Override
    public List<Revenues> getRevenueByYear(int year){
        return revenueRepository.findByRevenueYear(year);
    }
}
