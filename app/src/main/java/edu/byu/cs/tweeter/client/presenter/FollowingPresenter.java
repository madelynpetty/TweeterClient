package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements FollowService.GetFollowingObserver,
        UserService.GetUserObserver {

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }

    private static final int PAGE_SIZE = 10;
    private View view;
    private AuthToken authToken;
    private User targetUser;

    private User lastFollowee = null;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FollowingPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
        }
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
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

    //FOR GET FOLLOWING
    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages) {
//        view.setRecyclerViewVars(users, hasMorePages);
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    @Override
    public void getFollowingFailed(String message) {
        view.displayErrorMessage("Failed to get user's profile: " + message);
    }

    @Override
    public void getFollowingThrewException(Exception e) {
        view.displayErrorMessage("Failed to get user's profile because of exception: " + e);
    }
}
