package com.example.demo;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryLedgerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class InventoryConcurrencyTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryLedgerRepository ledgerRepository;
    private Long productId = 1L;
    @BeforeEach
    public void setUp() {
        // Reset our database product stock to exactly 100 before the test runs
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Test product not found! Make sure you inserted it in MySQL first."));
        product.setStockQuantity(100);
        productRepository.save(product);        
        // Clear old ledger entries to keep calculations clean
        ledgerRepository.deleteAll();
    }

    @Test
    public void simulateRaceCondition() throws InterruptedException {
        int numberOfThreads = 100; // 100 simultaneous shoppers
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);        
        // The latch is like a race official holding a starter pistol. 
        // It forces all 100 threads to wait until everyone is ready, then releases them ALL at once.
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                try {
                    latch.await(); // Wait here for the starter pistol!
                    inventoryService.purchaseProduct(productId, 1); // Buy 1 item
                } catch (Exception e) {
                    // Failures will happen here when we add locks, which is expected!
                    System.out.println("Purchase failed: " + e.getMessage());
                }
            });}
        // FIRE THE PISTOL! All 100 threads smash the database simultaneously
        latch.countDown();
        service.shutdown();        
        // Wait for all threads to finish their work before checking the results
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
        // If 100 people bought 1 item each from a stock of 100, remaining stock MUST be 0.
        // Without locks, this assertion WILL FAIL because threads overwrite each other's updates!
        assertEquals(0, finalProduct.getStockQuantity(), "Race condition detected! Inventory count is corrupted.");
    }
}