package com.fruitservice.model;

import java.io.Serializable;

/**
 * Model class representing a fruit and its price
 * Serializable so it can be sent over the network
 */
public class FruitPrice implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String fruitName;
    private double price;
    
    // Default constructor required for serialization
    public FruitPrice() {}
    
    public FruitPrice(String fruitName, double price) {
        this.fruitName = fruitName;
        this.price = price;
    }
    
    // Getter and setter methods
    public String getFruitName() {
        return fruitName;
    }
    
    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "FruitPrice{fruitName='" + fruitName + "', price=" + price + "}";
    }
}