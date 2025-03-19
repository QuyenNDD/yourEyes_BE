package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.InventoryResponse;
import com.example.myApp.enity.Inventory;
import com.example.myApp.enity.Products;
import com.example.myApp.repository.InventoryRepository;
import com.example.myApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryResponse> getInventoryReport() {
        List<Inventory> inventories = inventoryRepository.findAllByOrderByStockQuantityAsc();
        return inventories.stream().map(inventory -> {
            Products products = inventory.getProducts();
            return new InventoryResponse(
                    products.getName(),
                    inventory.getStockQuantity(),
                    inventory.getLastUpdated()
            );
        }).collect(Collectors.toList());
    }
}
