package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {
    public interface View extends PagedView<Status> {}

    private FeedPresenter.View view;

    public FeedPresenter(View view, User user) {
        super(view, user);
        this.view = view;
    }

    @Override
    public void loadItems() {
        new StatusService().getFeed(this, user, (Status) lastItem);
    }
}
