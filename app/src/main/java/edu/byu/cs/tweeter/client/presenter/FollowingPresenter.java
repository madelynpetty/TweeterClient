package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {

    public interface View extends PagedView<User> {}

    private static final int PAGE_SIZE = 10;
    private View view;

    public FollowingPresenter(View view, User targetUser) {
        super(view, targetUser);
        this.view = view;
    }

    @Override
    public void loadItems() {
        new FollowService().getFollowing(user, PAGE_SIZE, lastItem, this);
    }
}
