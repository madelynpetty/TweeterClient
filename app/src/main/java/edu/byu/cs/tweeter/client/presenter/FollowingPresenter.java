package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements FollowService.GetFollowingObserver {

    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages) {

    }

    @Override
    public void getFollowingFailed(String message) {

    }

    @Override
    public void getFollowingThrewException(Exception e) {

    }

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(String user);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }

    private static final int PAGE_SIZE = 10;
    private View view;
    private AuthToken authToken;
    private User targetUser;

    private User lastFollowee;
    private boolean hasMorePages;
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

    public void gotoUser(String alias) {
        view.navigateToUser(alias);
    }
}
