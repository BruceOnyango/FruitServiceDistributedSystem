package com.fruitservice.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the compute engine
 * This allows clients to execute tasks remotely via RMI
 */
public interface Compute extends Remote {
    /**
     * Execute a task remotely
     * @param t The task to execute
     * @return The result of the task execution
     * @throws RemoteException if network communication fails
     */
    <T> T executeTask(Task<T> t) throws RemoteException;
    
}