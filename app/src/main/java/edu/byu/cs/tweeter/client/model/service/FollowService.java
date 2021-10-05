package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    private static final int PAGE_SIZE = 10;

    //GET FOLLOWING

    public interface GetFollowingObserver {
        void getFollowingSucceeded(List<User> users, boolean hasMorePages, User lastFollowee);
        void getFollowingFailed(String message);
        void getFollowingThrewException(Exception e);
    }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {
        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                User lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;

                observer.getFollowingSucceeded(followees, hasMorePages, lastFollowee);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFollowingFailed(message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFollowingThrewException(ex);
            }
        }
    }


    //GET FOLLOWERS

    public interface GetFollowersObserver {
        void getFollowerSucceeded(List<User> followers, boolean hasMorePages, User lastFollower);
        void getFollowerFailed(String message);
        void getFollowerThrewException(Exception e);
    }

    public static void getFollowers(GetFollowersObserver observer, User user, User lastFollower) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowersHandler extends Handler {
        GetFollowersObserver observer;

        GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;

                observer.getFollowerSucceeded(followers, hasMorePages, lastFollower);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowerFailed(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowerThrewException(ex);
            }
        }
    }


    //FOLLOW

    public interface FollowObserver {
        void follow(User user);
        void followFailed(String message);
        void followThrewException(Exception e);
        void setFollowButton(boolean enabled);
        void updateFollowButton(boolean removed);
        void callUpdateSelectedUserFollowingAndFollowers(User user);
    }

    private User selectedUser;

    public void follow(FollowService.FollowObserver observer, User selectedUser) {
        this.selectedUser = selectedUser;
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowService.FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    private class FollowHandler extends Handler {
        private FollowService.FollowObserver observer;
        public FollowHandler(FollowService.FollowObserver  observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.callUpdateSelectedUserFollowingAndFollowers(selectedUser);
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

    public void updateSelectedUserFollowingAndFollowers(FollowerCountObserver followerCountObserver,
                                                        FollowingCountObserver followingCountObserver,
                                                        User selectedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowService.GetFollowersCountHandler(followerCountObserver));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowService.GetFollowingCountHandler(followingCountObserver));
        executor.execute(followingCountTask);
    }


    //UNFOLLOW

    public interface UnfollowObserver {
        void unfollowFailed(String message);
        void unfollowThrewException(Exception e);
        void setFollowButton(boolean enabled);
        void updateFollowButton(boolean removed);
        void callUpdateSelectedUserFollowingAndFollowers(User user);
    }

    public void unfollow(FollowService.UnfollowObserver observer, User selectedUser) {
        this.selectedUser = selectedUser;
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    private class UnfollowHandler extends Handler {
        private UnfollowObserver observer;
        public UnfollowHandler(UnfollowObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.callUpdateSelectedUserFollowingAndFollowers(selectedUser);
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

    public interface FollowerCountObserver {
        void setFollowerCount(int count);
        void followerCountFailed(String message);
        void followerCountThrewException(Exception e);
    }

    private class GetFollowersCountHandler extends Handler {
        private FollowerCountObserver observer;
        public GetFollowersCountHandler(FollowerCountObserver observer) {
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

    public interface FollowingCountObserver {
        void setFollowingCount(int count);
        void followingCountFailed(String message);
        void followingCountThrewException(Exception e);
    }

    private class GetFollowingCountHandler extends Handler {
        private FollowingCountObserver observer;
        public GetFollowingCountHandler(FollowingCountObserver observer) {
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


    public interface IsFollowerObserver {
        void setIsFollowerButton();
        void setIsNotFollowerButton();
        void isFollowerFailed(String message);
        void isFollowerThrewException(Exception e);
    }

    public void isFollower(IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    private class IsFollowerHandler extends Handler {
        private IsFollowerObserver observer;
        public IsFollowerHandler(IsFollowerObserver observer) {
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
