package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PresenterView<User> implements FollowService.GetFollowersObserver,
        UserService.GetUserObserver {

    public interface View extends PagedView<User> {}

    private FollowerPresenter.View view;

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

            new FollowService().getFollowers(this, user, lastItem);
        }
    }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }


    @Override
    public void getFollowerSucceeded(List<User> followers, boolean hasMorePages, User lastFollower) {
        view.setLoading(false);
        view.addItems(followers);
        this.hasMorePages = hasMorePages;
        this.lastItem = lastFollower;
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
