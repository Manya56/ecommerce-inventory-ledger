package com.example.demo;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryLedgerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.InventoryService;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@Transactional
public class InventoryConcurrencyTest {


// try to use mock object | this kind of test cases is not recomended 
    // it cause more deley to start the application 
    
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryLedgerRepository ledgerRepository;
    private Long productId = 1L;
    @BeforeEach
    public void setUp() {
        // Clear ledger tables cleanly
        ledgerRepository.deleteAll();
        
        // Check if test product exists, if not, create a new one cleanly
        Product product = productRepository.findById(productId).orElse(new Product());
        product.setId(productId); // Ensure it sets ID to 1L
        product.setStockQuantity(100); // Set it explicitly to an Integer number, not null
        // Add any other required fields your Product model has (e.g., name, price)
         
        
        productRepository.saveAndFlush(product);
    }
    @Test
    public void simulateRaceCondition() throws InterruptedException {
        int numberOfThreads = 100; // 100 simultaneous shoppers
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        
        // 1. Tracks when ALL 100 threads are spawned and waiting at the starting line
        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        
        // 2. The starter pistol that releases all threads at the exact same instant
        CountDownLatch startLatch = new CountDownLatch(1);
        
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                try {
                    readyLatch.countDown(); // Tell the main thread: "I am ready at the gate!"
                    startLatch.await();    // Freeze right here until the pistol is fired!
                    
                    inventoryService.purchaseProduct(productId, 1);
                } catch (Exception e) {
                    // If you implement Optimistic Locking, failures are expected and caught here
                    System.out.println("Purchase failed: " + e.getMessage());
                }
            });
        }
        
        // Wait until every single one of the 100 threads is lined up and waiting
        readyLatch.await(); 
        
        // --- FIRE THE PISTOL! ---
        // All 100 threads burst forward and smash the database simultaneously
        startLatch.countDown(); 
        
        service.shutdown();
        
        // Wait for all threads to finish their work before checking results
        while (!service.isTerminated()) {
            Thread.sleep(10);
        }
        
        // --- THE VERDICT ---
        Product finalProduct = productRepository.findById(productId).get();
        long ledgerCount = ledgerRepository.count();
        
        System.out.println("=========================================");
        System.out.println("TEST COMPLETED!");
        System.out.println("Initial Stock: 100");
        System.out.println("Total Purchase Attempts: 100");
        System.out.println("Actual Final Stock in DB: " + finalProduct.getStockQuantity());
        System.out.println("Total Ledger Rows Recorded: " + ledgerCount);
        System.out.println("=========================================");
        
        // If your backend service is properly thread-safe, this assertion will pass (Green Bar!)
        assertEquals(0, finalProduct.getStockQuantity(), "Race condition detected! Inventory count is incorrect.");
    }
}
