package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {
    public interface View extends PagedView<Status> {}

    private StoryPresenter.View view;

    public StoryPresenter(View view, User user) {
        super(view, user);
        this.view = view;
    }

    public void loadItems() {
        new StatusService().getStory(this, user, lastItem);
    }
}
