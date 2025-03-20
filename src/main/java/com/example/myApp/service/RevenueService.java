package com.example.myApp.service;

import com.example.myApp.enity.Revenues;

import java.util.List;

public interface RevenueService {
    List<Revenues> getRevenueByYear(int year);
    List<Revenues> getRevenueByMonthAndYear(int month, int year);
}
