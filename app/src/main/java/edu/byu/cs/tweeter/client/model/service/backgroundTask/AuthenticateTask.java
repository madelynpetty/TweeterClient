package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask {
    private static final String LOG_TAG = "AuthenticateTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private String username;
    private String password;
    protected User user;
    protected AuthToken authToken;

    protected AuthenticateTask(String username, String password, Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    @Override
    protected final boolean runTask() throws IOException {
        Pair<User, AuthToken> loginResult = runAuthenticationTask();
        user = loginResult.getFirst();
        authToken = loginResult.getSecond();
        BackgroundTaskUtils.loadImage(user);
        return true;
    }

    protected abstract Pair<User, AuthToken> runAuthenticationTask();


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, this.user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}