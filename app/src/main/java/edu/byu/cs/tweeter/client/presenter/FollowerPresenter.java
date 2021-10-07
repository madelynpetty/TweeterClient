package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User> {

    public interface View extends PagedView<User> {}

    private FollowerPresenter.View view;

    public FollowerPresenter(View view, User user) {
        super(view, user);
        this.view = view;
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public void loadItems() {
        new FollowService().getFollowers(this, user, lastItem);
    }
}
