package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import edu.byu.cs.tweeter.client.backgroundTask.*;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService {
    public interface MainObserver {
        void postStatusSucceeded(String message);
        void postStatusFailed(String message);
        void postStatusThrewException(Exception e);

        void logoutSucceeded();
        void logoutFailed(String message);
        void logoutThrewException(Exception e);

        void follow(User user);
        void followFailed(String message);
        void followThrewException(Exception e);
        void setFollowButton(boolean enabled);
        void updateFollowButton(boolean removed);
        void callUpdateSelectedUserFollowingAndFollowers(User user);

        void unfollowFailed(String message);
        void unfollowThrewException(Exception e);

        void setFollowerCount(int count);
        void followerCountFailed(String message);
        void followerCountThrewException(Exception e);

        void setFollowingCount(int count);
        void followingCountFailed(String message);
        void followingCountThrewException(Exception e);

        void setIsFollowerButton();
        void setIsNotFollowerButton();
        void isFollowerFailed(String message);
        void isFollowerThrewException(Exception e);
    }

    //POST STATUS

    public void postStatus(String post, MainObserver observer) throws ParseException, MalformedURLException {
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(statusTask);
    }

    private class PostStatusHandler extends Handler {
        private MainObserver observer;

        public PostStatusHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(PostStatusTask.SUCCESS_KEY);
            if (success) {
                observer.postStatusSucceeded("Successfully Posted!");
            } else if (msg.getData().containsKey(PostStatusTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(PostStatusTask.MESSAGE_KEY);
                observer.postStatusFailed("Failed to post status: " + message);
            } else if (msg.getData().containsKey(PostStatusTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(PostStatusTask.EXCEPTION_KEY);
                observer.postStatusThrewException(ex);
            }
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                int index = findUrlEndIndex(word);
                word = word.substring(0, index);
                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    //LOGOUT

    public void logout(MainObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new MainService.LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }

    private class LogoutHandler extends Handler {
        private MainObserver observer;
        public LogoutHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.logoutSucceeded();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
                observer.logoutFailed(message);
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                observer.logoutThrewException(ex);
            }
        }
    }

    //FOLLOW

    private User selectedUser;

    public void follow(MainObserver observer, User selectedUser) {
        this.selectedUser = selectedUser;
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new MainService.FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    private class FollowHandler extends Handler {
        private MainObserver observer;
        public FollowHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                updateSelectedUserFollowingAndFollowers(observer, selectedUser);
                observer.updateFollowButton(false);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.followFailed(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.followThrewException(ex);
            }

            observer.setFollowButton(true);
        }
    }

    public void updateSelectedUserFollowingAndFollowers(MainObserver observer, User selectedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountHandler(observer));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountHandler(observer));
        executor.execute(followingCountTask);
    }

    //UNFOLLOW

    public void unfollow(MainObserver observer, String followButton) {
        observer.setFollowButton(false);

        if (followButton.equals("Following")) {
            UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                    selectedUser, new UnfollowHandler(observer));
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(unfollowTask);
        }
        else {
            observer.follow(selectedUser);
        }
    }

    private class UnfollowHandler extends Handler {
        private MainObserver observer;
        public UnfollowHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                updateSelectedUserFollowingAndFollowers(observer, selectedUser);
                observer.updateFollowButton(true);
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.unfollowFailed(message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.unfollowThrewException(ex);
            }
            observer.setFollowButton(true);
        }
    }

    //FOLLOWER COUNT
    //TODO I don't think I need a getFollowersCount() method since the only thing that calls it is inside this class

    private class GetFollowersCountHandler extends Handler {
        private MainObserver observer;
        public GetFollowersCountHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.setFollowerCount(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.followerCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.followerCountThrewException(ex);
            }
        }
    }

    //FOLLOWING COUNT
    //TODO I don't think I need a getFollowingCount() method since the only thing that calls it is inside this class

    private class GetFollowingCountHandler extends Handler {
        private MainObserver observer;
        public GetFollowingCountHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.setFollowingCount(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.followingCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.followingCountThrewException(ex);
            }
        }
    }

    //IS FOLLOWER

    public void isFollower(MainObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    private class IsFollowerHandler extends Handler {
        private MainObserver observer;
        public IsFollowerHandler(MainService.MainObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

                // If logged-in user is a follower of the selected user, display the follow button as "following"
                if (isFollower) {
                    observer.setIsFollowerButton();
                } else {
                    observer.setIsNotFollowerButton();
                }
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.isFollowerFailed(message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.isFollowerThrewException(ex);
            }
        }
    }

}
