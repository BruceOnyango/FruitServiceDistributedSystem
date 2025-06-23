package com.fruitservice.tasks;

import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import com.fruitservice.server.FruitComputeEngine;
import java.util.Map;

/**
 * Task to calculate the cost of a fruit given quantity
 */
public class CalFruitCost implements Task<String> {
    private static final long serialVersionUID = 1L;
    
    private String fruitName;
    private int quantity;
    
    public CalFruitCost(String fruitName, int quantity) {
        this.fruitName = fruitName;
        this.quantity = quantity;
    }
    
    public String execute() {
        try {
            Map<String, FruitPrice> table = FruitComputeEngine.getFruitPriceTable();
            
            // Check if fruit exists
            if (!table.containsKey(fruitName.toLowerCase())) {
                return "Error: Fruit " + fruitName + " not found in the table.";
            }
            
            // Calculate total cost
            FruitPrice fruit = table.get(fruitName.toLowerCase());
            double totalCost = fruit.getPrice() * quantity;
            
            // Return formatted result
            return "Fruit: " + fruit.getFruitName() + 
                   "\nUnit Price: $" + fruit.getPrice() + 
                   "\nQuantity: " + quantity + 
                   "\nTotal Cost: $" + String.format("%.2f", totalCost);
                   
        } catch (Exception e) {
            return "Error: Failed to calculate fruit cost - " + e.getMessage();
        }
    }
}