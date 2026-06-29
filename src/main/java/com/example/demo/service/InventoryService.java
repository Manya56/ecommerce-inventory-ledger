package com.example.demo.service;
import com.example.demo.entity.InventoryLedger;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryLedgerRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InventoryService {
    @Autowired
    private ProductRepository productRepository;  // use always constructor  or setter injection

    @Autowired
    private InventoryLedgerRepository ledgerRepository;
    /**
     * This method handles a basic purchase. 
     * Right now, it has NO CONCURRENCY CONTROL. It is vulnerable to race conditions!
     */
    @Transactional   // learn about propogation and isolation levels and why 
    public void purchaseProduct(Long productId, Integer quantity) {
        // Change findById() to findByIdWithPessimisticLock()
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));   // always try to use DTO/DAO class

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Out of stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
      //  productRepository.save(product);   not mandatory 

        InventoryLedger ledgerEntry = new InventoryLedger();  // always try to use DTO/DAO class
        ledgerEntry.setProductId(productId);
        ledgerEntry.setQuantityChange(-quantity);
        ledgerEntry.setTransactionType("SALE");
        ledgerRepository.save(ledgerEntry);
    }
}
