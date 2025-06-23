package com.fruitservice.model;

import java.io.Serializable;

/**
 * Represents an item in a purchase (fruit name + quantity)
 */
public class PurchaseItem implements Serializable {
    private static final long serialVersionUID = 1L;//ensures compatibility when sending objects over network
    
    private String fruitName;
    private int quantity;
    
    public PurchaseItem() {}
    
    public PurchaseItem(String fruitName, int quantity) {
        this.fruitName = fruitName;
        this.quantity = quantity;
    }
    
    public String getFruitName() {
        return fruitName;
    }
    
    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}