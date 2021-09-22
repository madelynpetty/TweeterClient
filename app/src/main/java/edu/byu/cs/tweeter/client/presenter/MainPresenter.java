package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.MainService;
import edu.byu.cs.tweeter.model.domain.User;

import java.net.MalformedURLException;
import java.text.ParseException;

public class MainPresenter implements MainService.MainObserver {

    public interface View {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);

        void logout();

        void updateFollowButton(boolean removed);
        void setFollowButton(boolean enabled);

        void setFollowerCount(int count);
        void setFollowingCount(int count);

        void setIsFollowerButton();
        void setIsNotFollowerButton();
    }

    private MainPresenter.View view;

    public MainPresenter(MainPresenter.View view) {
        this.view = view;
    }

    public void postStatus(String post) throws ParseException, MalformedURLException {
        view.displayInfoMessage("Posting Status...");
        new MainService().postStatus(post, this);
    }

    public void logout() {
        view.displayInfoMessage("Logging Out...");
        new MainService().logout(this);
    }

    @Override
    public void follow(User user) {
        view.displayInfoMessage("Adding " + user.getName() + "...");
        new MainService().follow(this, user);
    }

    public void unfollow(String followButton, String name) {
        view.displayInfoMessage("Removing " + name + "...");
        new MainService().unfollow(this, followButton);
    }

    public void isFollower() {
        new MainService().isFollower(this);
    }

    @Override
    public void postStatusSucceeded(String message) { //this might need to be a user
        view.displayInfoMessage(message);
    }

    @Override
    public void postStatusFailed(String message) {
        view.displayErrorMessage("Failed to post status: " + message);
    }

    @Override
    public void postStatusThrewException(Exception e) {
        view.displayErrorMessage("Failed to post status because of exception: " + e.getMessage());
    }

    @Override
    public void logoutSucceeded() {
        view.logout();
    }

    @Override
    public void logoutFailed(String message) {
        view.displayErrorMessage("Failed to logout: " + message);
    }

    @Override
    public void logoutThrewException(Exception e) {
        view.displayErrorMessage("Failed to logout because of exception: " + e.getMessage());
    }

    @Override
    public void updateFollowButton(boolean removed) {
        view.updateFollowButton(false);
    }

    @Override
    public void callUpdateSelectedUserFollowingAndFollowers(User user) {
        new MainService().updateSelectedUserFollowingAndFollowers(this, user);
    }

    @Override
    public void followFailed(String message) {
        view.displayInfoMessage("Failed to follow: " + message);
    }

    @Override
    public void followThrewException(Exception ex) {
        view.displayInfoMessage("Failed to follow because of exception: " + ex.getMessage());
    }

    @Override
    public void setFollowButton(boolean enabled) {
        view.setFollowButton(enabled);
    }

    @Override
    public void unfollowFailed(String message) {
        view.displayInfoMessage("Failed to unfollow: " + message);
    }

    @Override
    public void unfollowThrewException(Exception e) {
        view.displayErrorMessage("Failed to unfollow because of exception: " + e.getMessage());
    }

    @Override
    public void setFollowerCount(int count) {
        view.setFollowerCount(count);
    }

    @Override
    public void followerCountFailed(String message) {
        view.displayInfoMessage("Failed to get followers count: " + message);
    }

    @Override
    public void followerCountThrewException(Exception e) {
        view.displayErrorMessage("Failed to get followers count because of exception: " + e.getMessage());
    }

    @Override
    public void setFollowingCount(int count) {
        view.setFollowingCount(count);
    }

    @Override
    public void followingCountFailed(String message) {
        view.displayInfoMessage("Failed to get following count: " + message);
    }

    @Override
    public void followingCountThrewException(Exception e) {
        view.displayErrorMessage("Failed to get following count because of exception: " + e.getMessage());
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
    public void isFollowerFailed(String message) {
        view.displayInfoMessage("Failed to determine following relationship: " + message);
    }

    @Override
    public void isFollowerThrewException(Exception e) {
        view.displayErrorMessage("Failed to determine following relationship because of exception: " + e.getMessage());
    }
}
