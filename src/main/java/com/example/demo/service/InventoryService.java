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
    private ProductRepository productRepository;

    @Autowired
    private InventoryLedgerRepository ledgerRepository;
    /**
     * This method handles a basic purchase. 
     * Right now, it has NO CONCURRENCY CONTROL. It is vulnerable to race conditions!
     */
    @Transactional
    public void purchaseProduct(Long productId, Integer quantity) {
        // Change findById() to findByIdWithPessimisticLock()
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Out of stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        InventoryLedger ledgerEntry = new InventoryLedger();
        ledgerEntry.setProductId(productId);
        ledgerEntry.setQuantityChange(-quantity);
        ledgerEntry.setTransactionType("SALE");
        ledgerRepository.save(ledgerEntry);
    }
}
