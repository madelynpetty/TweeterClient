package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PresenterView<Status> implements StatusService.StoryObserver,
        UserService.GetUserObserver {

    public interface View extends PagedView<Status> {}

    private StoryPresenter.View view;

    public StoryPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new StatusService().getStory(this, user, lastItem);
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
    public void storySucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.lastItem = lastStatus;
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
