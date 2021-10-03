package edu.byu.cs.tweeter.client.model.service;

public interface ServiceObserver {
    void handleFailed(String message);
    void handleException(Exception ex);
}
