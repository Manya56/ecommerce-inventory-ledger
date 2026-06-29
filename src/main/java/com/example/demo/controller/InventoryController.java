package com.example.demo.controller;

import com.example.demo.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // import everything is not a good practice 
/*
public final class ApiPaths {

    private ApiPaths() {}

    public static final String INVENTORY_API = "/api/inventory";
}

@RequestMapping(ApiPaths.INVENTORY_API)
*/

@RestController
@RequestMapping("/api/inventory") // use constants 
public class InventoryController {

    @Autowired
    private InventoryService inventoryService; // avoid fiedl injection 

    // URL: http://localhost:8080/api/inventory/buy?productId=1&quantity=1
    @PostMapping("/buy")  //Use constants for API paths because Spring needs fixed values when the application starts.
    public ResponseEntity<String> buyProduct(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            inventoryService.purchaseProduct(productId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
