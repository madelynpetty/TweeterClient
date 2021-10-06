package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteTask<T> {
    public ExecuteTask(T task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute((Runnable) task);
    }

    public ExecuteTask(T task1, T task2) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute((Runnable) task1);
        executor.execute((Runnable) task2);
    }
}