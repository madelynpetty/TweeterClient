package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StatusService.StoryObserver,
        UserService.GetUserObserver {

    public interface View {
        void addItems(List<Status> statuses);
        void displayMessage(String message);
        void setLoading(boolean isLoading);
        void navigateToUser(User user);
    }

    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private User user;
    private Status lastStatus = null;
    private StoryPresenter.View view;

    public StoryPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new StatusService().getStory(this, user, lastStatus);
        }
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
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

    @Override
    public void storySucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.lastStatus = lastStatus;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    @Override
    public void storyFailed(String message) {
        view.displayMessage("Failed to get story: " + message);
    }

    @Override
    public void storyThrewException(Exception e) {
        view.displayMessage("Failed to get story because of exception: " + e.getMessage());
    }
}
