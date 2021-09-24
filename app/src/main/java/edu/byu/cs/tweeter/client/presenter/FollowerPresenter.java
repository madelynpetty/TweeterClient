package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowService.GetFollowersObserver,
        UserService.GetUserObserver {

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(User user);
        void displayMessage(String message);
    }

    private View view;
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    private User user;
    private User lastFollower = null;

    public FollowerPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void gotoUser(User user) {
        view.navigateToUser(user);
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new FollowService().getFollowers(this, user, lastFollower);
        }
    }

    //FOR GET USERS
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

    //FOR GET FOLLOWER

    @Override
    public void getFollowerSucceeded(List<User> followers, boolean hasMorePages, User lastFollower) {
        view.setLoading(false);
        view.addItems(followers);
        this.hasMorePages = hasMorePages;
        isLoading = false;
    }

    @Override
    public void getFollowerFailed(String message) {
        view.displayMessage("Failed to get followers: " + message);
    }

    @Override
    public void getFollowerThrewException(Exception e) {
        view.displayMessage("Failed to get followers because of exception: " + e.getMessage());
    }
}
