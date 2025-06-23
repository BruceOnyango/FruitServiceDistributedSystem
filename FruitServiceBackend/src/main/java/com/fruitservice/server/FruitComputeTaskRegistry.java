package com.fruitservice.server;

import com.fruitservice.interfaces.Compute;
import com.fruitservice.interfaces.Task;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Helper class to execute tasks on the remote compute engine
 * This is used by servlets to communicate with the RMI server
 */
public class FruitComputeTaskRegistry {
    
    private static final String RMI_HOST = "localhost"; // Change this for remote servers
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "FruitComputeEngine";
    
    /**
     * Execute a task on the remote compute engine
     * @param task The task to execute
     * @return The result of task execution
     */
    public static <T> T executeTask(Task<T> task) {
        try {
            System.out.println("Connecting to RMI server at " + RMI_HOST + ":" + RMI_PORT);
            
            // Get the RMI registry
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Look up the compute engine
            Compute comp = (Compute) registry.lookup(SERVICE_NAME);
            
            // Execute the task remotely
            System.out.println("Executing task: " + task.getClass().getSimpleName());
            T result = comp.executeTask(task);
            
            System.out.println("Task completed successfully");
            return result;
            
        } catch (Exception e) {
            System.err.println("Failed to execute task: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}