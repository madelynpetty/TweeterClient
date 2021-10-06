package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PresenterView<User> implements FollowService.GetFollowingObserver,
        UserService.GetUserObserver {

    public interface View extends PagedView<User> {}

    private static final int PAGE_SIZE = 10;
    private View view;

    public FollowingPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.user = targetUser;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new FollowService().getFollowing(user, PAGE_SIZE, lastItem, this);
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

    //FOR GET FOLLOWING
    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages, User lastFollowee) {
        view.setLoading(false);
        view.addItems(users);
        this.lastItem = lastFollowee;
        this.hasMorePages = hasMorePages;
        if (hasMorePages) {
            isLoading = false;
        }
    }

    @Override
    public void handleFailed(String message) {
        view.displayMessage("Failed: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayMessage("Failed because of exception: " + ex.getMessage());
    }
}
