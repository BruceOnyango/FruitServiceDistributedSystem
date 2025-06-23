package com.fruitservice.server;

import com.fruitservice.interfaces.Compute;
import com.fruitservice.interfaces.Task;
import com.fruitservice.model.FruitPrice;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Main RMI server that executes tasks
 * Extends UnicastRemoteObject to be accessible via RMI
 */
public class FruitComputeEngine extends UnicastRemoteObject implements Compute {
    
    // Thread-safe map to store fruit prices
    // Static so all instances share the same data
    private static final Map<String, FruitPrice> fruitPriceTable = new ConcurrentHashMap<>();
    
    /**
     * Constructor - initializes with sample data
     * @throws RemoteException if RMI setup fails
     */
    public FruitComputeEngine() throws RemoteException {
        super(); // Call UnicastRemoteObject constructor
        
        // Initialize with some sample data
        fruitPriceTable.put("apple", new FruitPrice("apple", 2.50));
        fruitPriceTable.put("banana", new FruitPrice("banana", 1.20));
        fruitPriceTable.put("orange", new FruitPrice("orange", 3.00));
        
        System.out.println("FruitComputeEngine initialized with sample data:");
        fruitPriceTable.forEach((key, value) -> 
            System.out.println("  " + key + " -> $" + value.getPrice()));
    }
    
    /**
     * Execute a task remotely
     * This method is called by clients over the network
     */
    public <T> T executeTask(Task<T> t) throws RemoteException {
        System.out.println("Executing task: " + t.getClass().getSimpleName());
        
        try {
            T result = t.execute();
            System.out.println("Task completed successfully");
            return result;
        } catch (Exception e) {
            System.err.println("Task execution failed: " + e.getMessage());
            throw new RemoteException("Task execution failed", e);
        }
    }
    
    /**
     * Get the shared fruit price table
     * Static method so tasks can access it
     */
    public static Map<String, FruitPrice> getFruitPriceTable() {
        return fruitPriceTable;
    }
    
    /**
     * Main method to start the RMI server
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting FruitComputeEngine...");
            
            // Create the compute engine
            FruitComputeEngine engine = new FruitComputeEngine();
            
            // Create RMI registry on port 1099
            Registry registry = LocateRegistry.getRegistry(1099);
            
            // Bind the engine to the registry with name "FruitComputeEngine"
            registry.bind("FruitComputeEngine", engine);
            
            System.out.println("FruitComputeEngine bound and ready on port 1099");
            System.out.println("Waiting for client requests...");
            
        } catch (Exception e) {
            System.err.println("FruitComputeEngine startup failed:");
            e.printStackTrace();
        }
    }
}