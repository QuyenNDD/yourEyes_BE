package com.example.myApp.controller;

import com.example.myApp.enity.Revenues;
import com.example.myApp.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/revenues")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Revenues>> getRevenueByYear(@PathVariable int year){
        return ResponseEntity.ok(revenueService.getRevenueByYear(year));
    }

    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<List<Revenues>> getRevenueByMonthAndYear(@PathVariable int month, @PathVariable int year){
        return ResponseEntity.ok(revenueService.getRevenueByMonthAndYear(month, year));
    }
}
