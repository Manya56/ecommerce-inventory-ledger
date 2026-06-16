package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class Product{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="product_id", nullable=false)
	private Long productId;
	@Column(name="quantity_change", nullable=false)
	private Integer quantityChange;
	@Column(name="transaction_type", nullable=false)
	private String transactionType;
	@Column(name="created_at", insertable=false,updatable=false)
	private LocalDateTime createdAt;
	


    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public Integer getStockQuantity() {
		// TODO Auto-generated method stub
		if(this.quantityChange==null) {
			return 0;
	}
		return this.quantityChange;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStockQuantity(int i) {
		// TODO Auto-generated method stub
		
	}
}
	
