package com.fruitservice.tasks;

import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import com.fruitservice.server.FruitComputeEngine;
import java.util.Map;

/**
 * Task to add a new fruit to the price table
 */
public class AddFruitPrice implements Task<String> {
    private static final long serialVersionUID = 1L;
    
    private String fruitName;
    private double price;
    
    public AddFruitPrice(String fruitName, double price) {
        this.fruitName = fruitName;
        this.price = price;
    }
    
    /**
     * Execute the add fruit operation
     * @return Success or error message
     */
    public String execute() {
        try {
            // Get the shared fruit price table
            Map<String, FruitPrice> table = FruitComputeEngine.getFruitPriceTable();
            
            // Check if fruit already exists
            if (table.containsKey(fruitName.toLowerCase())) {
                return "Error: Fruit " + fruitName + " already exists in the table.";
            }
            
            // Add the new fruit
            table.put(fruitName.toLowerCase(), new FruitPrice(fruitName, price));
            return "Success: Added " + fruitName + " with price $" + price;
            
        } catch (Exception e) {
            return "Error: Failed to add fruit - " + e.getMessage();
        }
    }
}