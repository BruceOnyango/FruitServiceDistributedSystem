package com.fruitservice.client;

import com.fruitservice.model.PurchaseItem;
import com.fruitservice.server.FruitComputeTaskRegistry;
import com.fruitservice.tasks.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test client to verify the RMI server works
 */
public class TestClient {
    
    public static void main(String[] args) {
        System.out.println("=== Fruit Service Engine Test Client ===\n");
        
        // Test 1: Add a new fruit
        System.out.println("1. Adding new fruit (mango):");
        AddFruitPrice addTask = new AddFruitPrice("mango", 4.50);
        String result1 = FruitComputeTaskRegistry.executeTask(addTask);
        System.out.println(result1 + "\n");
        
        // Test 2: Update fruit price
        System.out.println("2. Updating apple price:");
        UpdateFruitPrice updateTask = new UpdateFruitPrice("apple", 2.75);
        String result2 = FruitComputeTaskRegistry.executeTask(updateTask);
        System.out.println(result2 + "\n");
        
        // Test 3: Calculate fruit cost
        System.out.println("3. Calculating cost for 5 apples:");
        CalFruitCost costTask = new CalFruitCost("apple", 5);
        String result3 = FruitComputeTaskRegistry.executeTask(costTask);
        System.out.println(result3 + "\n");
        
        // Test 4: Generate receipt
        System.out.println("4. Generating receipt:");
        List<PurchaseItem> items = new ArrayList<>();
        items.add(new PurchaseItem("apple", 3));
        items.add(new PurchaseItem("banana", 2));
        items.add(new PurchaseItem("mango", 1));
        
        CalculateCost receiptTask = new CalculateCost(items, 20.00, "John Doe");
        String result4 = FruitComputeTaskRegistry.executeTask(receiptTask);
        System.out.println(result4 + "\n");
        
        // Test 5: Delete a fruit
        System.out.println("5. Deleting mango:");
        DeleteFruitPrice deleteTask = new DeleteFruitPrice("mango");
        String result5 = FruitComputeTaskRegistry.executeTask(deleteTask);
        System.out.println(result5);
        
        System.out.println("\n=== All tests completed ===");
    }
}