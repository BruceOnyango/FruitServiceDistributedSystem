package com.fruitservice.tasks;

import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import com.fruitservice.model.PurchaseItem;
import com.fruitservice.server.FruitComputeEngine;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Task to generate a complete receipt for multiple items
 */
public class CalculateCost implements Task<String> {
    private static final long serialVersionUID = 1L;
    
    private List<PurchaseItem> items;
    private double amountPaid;
    private String cashierName;
    
    public CalculateCost(List<PurchaseItem> items, double amountPaid, String cashierName) {
        this.items = items;
        this.amountPaid = amountPaid;
        this.cashierName = cashierName;
    }
    
    public String execute() {
        try {
            Map<String, FruitPrice> table = FruitComputeEngine.getFruitPriceTable();
            StringBuilder receipt = new StringBuilder();
            double totalCost = 0.0;
            
            // Receipt header
            receipt.append("=== FRUIT STORE RECEIPT ===\n");
            receipt.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            receipt.append("Cashier: ").append(cashierName).append("\n");
            receipt.append("---------------------------\n");
            
            // Process each item
            for (PurchaseItem item : items) {
                String fruitKey = item.getFruitName().toLowerCase();
                
                // Check if fruit exists
                if (!table.containsKey(fruitKey)) {
                    return "Error: Fruit " + item.getFruitName() + " not found in the table.";
                }
                
                // Calculate item total
                FruitPrice fruit = table.get(fruitKey);
                double itemTotal = fruit.getPrice() * item.getQuantity();
                totalCost += itemTotal;
                
                // Add line to receipt
                receipt.append(String.format("%-10s x%d @ $%.2f = $%.2f\n", 
                    fruit.getFruitName(), item.getQuantity(), fruit.getPrice(), itemTotal));
            }
            
            // Receipt footer
            receipt.append("---------------------------\n");
            receipt.append(String.format("Total Cost: $%.2f\n", totalCost));
            receipt.append(String.format("Amount Paid: $%.2f\n", amountPaid));
            
            // Calculate change
            if (amountPaid < totalCost) {
                receipt.append(String.format("INSUFFICIENT PAYMENT! Short by: $%.2f\n", totalCost - amountPaid));
            } else {
                double change = amountPaid - totalCost;
                receipt.append(String.format("Change Due: $%.2f\n", change));
            }
            
            receipt.append("===========================\n");
            receipt.append("Thank you for your purchase!\n");
            
            return receipt.toString();
            
        } catch (Exception e) {
            return "Error: Failed to generate receipt - " + e.getMessage();
        }
    }
}