package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowService.GetFollowersObserver, UserService.GetUserObserver {

    public interface View {
        void switchToUser(User user);
        void displayMessage(String message);
        void showFollowers(List<User> followers, boolean hasMorePages, User lastFollower);
    }

    private View view;

    public FollowerPresenter(View view) {
        this.view = view;
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public void getFollowers(User user, User lastFollower) {
        FollowService.getFollowers(this, user, lastFollower);
    }


    //FOR GET USERS
    @Override
    public void getUserSucceeded(User user) {
        view.switchToUser(user);
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
        view.showFollowers(followers, hasMorePages, lastFollower);
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
