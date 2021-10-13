package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    private static final int PAGE_SIZE = 10;

    //FEED
    public interface FeedObserver extends ServiceObserver {
        void statusSucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException;
    }

    public static void getFeed(FeedObserver observer, User user, Status lastStatus) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetFeedHandler(observer));
        new ExecuteTask<>(getFeedTask);
    }

    private static class GetFeedHandler extends BackgroundTaskHandler {
        public GetFeedHandler(FeedObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccessMessage(Message msg) {
            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
            Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            try {
                ((FeedObserver)observer).statusSucceeded(statuses, hasMorePages, lastStatus);
            } catch (MalformedURLException e) {
                observer.handleException(e);
            }
        }
    }

    //STORY

    public interface StoryObserver extends ServiceObserver {
        void statusSucceeded(List<Status> statuses, boolean hasMorePages, Status lastStatus);
    }

    public static void getStory(StoryObserver observer, User user, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetStoryHandler(observer));
        new ExecuteTask<>(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends BackgroundTaskHandler {
        GetStoryHandler(StoryObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(Message msg) {
            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
            Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

            ((StoryObserver)observer).statusSucceeded(statuses, hasMorePages, lastStatus);
        }
    }

    //POST STATUS

    public interface PostStatusObserver extends ServiceObserver {
        void postStatusSucceeded(String message);
    }

    public void postStatus(String post, StatusService.PostStatusObserver observer) throws ParseException {
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(),
                parseURLs(post), parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        new ExecuteTask<>(statusTask);
    }

    private class PostStatusHandler extends BackgroundTaskHandler {
        public PostStatusHandler(StatusService.PostStatusObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccessMessage(Message msg) {
            ((PostStatusObserver)observer).postStatusSucceeded("Successfully Posted!");
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
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
}
