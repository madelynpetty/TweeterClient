package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> implements UserService.GetUserObserver,
        StatusService.FeedObserver, StatusService.StoryObserver,
        FollowService.GetFollowingObserver, FollowService.GetFollowersObserver {

    public interface PagedView<U> {
        void addItems(List<U> lastItems);
        void displayMessage(String message);
        void setLoading(boolean isLoading);
        void navigateToUser(User user);
    }

    protected PagedView view;
    protected boolean isLoading = false;
    protected boolean hasMorePages = true;
    protected User user;
    protected T lastItem = null;

    public PagedPresenter(PagedView view, User user) {
        this.view = view;
        this.user = user;
    }

    @Override
    public void statusSucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.lastItem = (T) lastStatus;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    @Override
    public void followSucceeded(List<User> users, boolean hasMorePages, User lastFollowee) {
        view.setLoading(false);
        view.addItems(users);
        this.lastItem = (T) lastFollowee;
        this.hasMorePages = hasMorePages;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    public void loadMoreItems() throws MalformedURLException {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            loadItems();
        }
    }

    public abstract void loadItems();

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        view.displayMessage("Failed: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayMessage("Exception: " + ex.getMessage());
    }

}
