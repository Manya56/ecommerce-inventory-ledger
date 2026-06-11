package com.example.demo.controller;

import com.example.demo.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // URL: http://localhost:8080/api/inventory/buy?productId=1&quantity=1
    @PostMapping("/buy")
    public ResponseEntity<String> buyProduct(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            inventoryService.purchaseProduct(productId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}