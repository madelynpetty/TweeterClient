package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    public interface View {
        void navigateToUser(User user);
        void uploadingImage();

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private RegisterPresenter.View view;

    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView image) {
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateRegistration(firstName, lastName, alias, password, image);

        if (message == null) {
            view.displayInfoMessage("Registering...");
            new UserService().register(firstName, lastName, alias, password, image, this);
        }
        else {
            view.displayErrorMessage("Login failed: " + message);
        }
    }

    public void uploadImage() {
        view.uploadingImage();
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageToUpload.getDrawable() == null) {
            return "Profile image must be uploaded.";
        }

        return null;
    }

    @Override
    public void registerSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage("Failed to register: " + message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayErrorMessage("Failed to register because of exception: " + ex.getMessage());
    }
}
