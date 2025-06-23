package com.fruitservice.tasks;

import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import com.fruitservice.server.FruitComputeEngine;
import java.util.Map;

/**
 * Task to delete a fruit from the price table
 */
public class DeleteFruitPrice implements Task<String> {
    private static final long serialVersionUID = 1L;
    
    private String fruitName;
    
    public DeleteFruitPrice(String fruitName) {
        this.fruitName = fruitName;
    }
    
    public String execute() {
        try {
            Map<String, FruitPrice> table = FruitComputeEngine.getFruitPriceTable();
            
            // Check if fruit exists
            if (!table.containsKey(fruitName.toLowerCase())) {
                return "Error: Fruit " + fruitName + " not found in the table.";
            }
            
            // Remove the fruit
            FruitPrice removedFruit = table.remove(fruitName.toLowerCase());
            return "Success: Deleted " + removedFruit.getFruitName() + " (was $" + removedFruit.getPrice() + ")";
            
        } catch (Exception e) {
            return "Error: Failed to delete fruit - " + e.getMessage();
        }
    }
}