package com.fruitservice.tasks;

import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import com.fruitservice.server.FruitComputeEngine;
import java.util.Map;

/**
 * Task to update an existing fruit's price
 */
public class UpdateFruitPrice implements Task<String> {
    private static final long serialVersionUID = 1L;
    
    private String fruitName;
    private double newPrice;
    
    public UpdateFruitPrice(String fruitName, double newPrice) {
        this.fruitName = fruitName;
        this.newPrice = newPrice;
    }
    
    public String execute() {
        try {
            Map<String, FruitPrice> table = FruitComputeEngine.getFruitPriceTable();
            
            // Check if fruit exists
            if (!table.containsKey(fruitName.toLowerCase())) {
                return "Error: Fruit " + fruitName + " not found in the table.";
            }
            
            // Update the price
            FruitPrice fruit = table.get(fruitName.toLowerCase());
            double oldPrice = fruit.getPrice();
            fruit.setPrice(newPrice);
            
            return "Success: Updated " + fruitName + " price from $" + oldPrice + " to $" + newPrice;
            
        } catch (Exception e) {
            return "Error: Failed to update fruit price - " + e.getMessage();
        }
    }
}