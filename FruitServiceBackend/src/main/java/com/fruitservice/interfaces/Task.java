package com.fruitservice.interfaces;

import java.io.Serializable;

/**
 * Interface for tasks that can be executed remotely
 * Serializable allows the task to be sent over the network
 */
public interface Task<T> extends Serializable {
    /**
     * Execute the task and return result
     * @return The result of task execution
     */
    T execute();
}