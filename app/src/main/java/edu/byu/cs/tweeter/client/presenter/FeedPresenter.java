package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements StatusService.FeedObserver,
        UserService.GetUserObserver {

    @Override
    public void handleFailed(String message) {
        view.displayMessage("Failed to get feed: " + message);
    }

    @Override
    public void handleException(Exception ex) {

    }

    public interface View {
        void navigateToUser(User user);
        void setLoading(boolean value) throws MalformedURLException;
        void displayMessage(String message);
        void addItems(List<Status> statuses);
    }

    private View view;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private User user;
    private Status lastStatus = null;

    public FeedPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public void loadMoreItems() throws MalformedURLException {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new StatusService().getFeed(this, user, lastStatus);
        }
    }

    @Override
    public void feedSucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.lastStatus = lastStatus;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {

    }

    @Override
    public void getUserThrewException(Exception e) {

    }
}
