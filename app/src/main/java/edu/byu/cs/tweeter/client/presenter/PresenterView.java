package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PresenterView<T> {
    protected boolean isLoading = false;
    protected boolean hasMorePages = true;
    protected User user;
    protected T lastItem = null;

    public interface PagedView<U> {
        void addItems(List<U> lastItems);
        void displayMessage(String message);
        void setLoading(boolean isLoading);
        void navigateToUser(User user);
    }
}
