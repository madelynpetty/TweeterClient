package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements StatusService.PostStatusObserver,
        UserService.LogoutObserver, FollowService.FollowObserver, FollowService.UnfollowObserver,
        FollowService.FollowerCountObserver, FollowService.FollowingCountObserver,
        FollowService.IsFollowerObserver {

    public interface View {
        void logout();

        void updateFollowButton(boolean removed);
        void setFollowButton(boolean enabled);

        void setFollowerCount(int count);
        void setFollowingCount(int count);

        void setIsFollowerButton();
        void setIsNotFollowerButton();

        void displayInfoMessage(String message);
    }

    private MainPresenter.View view;
    private StatusService statusService; //for testing purposes only

    public MainPresenter(MainPresenter.View view) {
        this.view = view;
        this.statusService = new StatusService();
    }

    public void postStatus(String post) throws ParseException {
        view.displayInfoMessage("Posting Status...");
        getStatusService().postStatus(post, this);
    }

    public void logout() {
        Cache.getInstance().clearCache();
        view.displayInfoMessage("Logging Out...");
        new UserService().logout(this);
    }

    public StatusService getStatusService() { //for testing purposes only
        if (statusService == null) return new StatusService();
        return statusService;
    }

    @Override
    public void follow(User user) {
        view.displayInfoMessage("Adding " + user.getName() + "...");
        new FollowService().follow(this, user);
    }

    public void unfollow(String name, User user) {
        view.displayInfoMessage("Removing " + name + "...");
        new FollowService().unfollow(this, user);
    }

    public void isFollower() {
        new FollowService().isFollower(this);
    }

    @Override
    public void postStatusSucceeded(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void logoutSucceeded() {
        view.logout();
    }

    @Override
    public void updateFollowButton(boolean removed) {
        view.updateFollowButton(removed);
    }

    @Override
    public void callUpdateSelectedUserFollowingAndFollowers(User user) {
        new FollowService().updateSelectedUserFollowingAndFollowers(this, this, user);
    }

    @Override
    public void setFollowButton(boolean enabled) {
        view.setFollowButton(enabled);
    }

    @Override
    public void setFollowerCount(int count) {
        view.setFollowerCount(count);
    }

    @Override
    public void setFollowingCount(int count) {
        view.setFollowingCount(count);
    }

    @Override
    public void setIsFollowerButton() {
        view.setIsFollowerButton();
    }

    @Override
    public void setIsNotFollowerButton() {
        view.setIsNotFollowerButton();
    }

    @Override
    public void handleFailed(String message) {
        view.displayInfoMessage("Failed: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayInfoMessage("Failed because of exception: " + ex.getMessage());
    }
}
