package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.entity.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.logging.Logger;

/**
 * Controller for the Settings screen that displays information about the currently logged-in user.
 */
public class SettingsScreenController {

    private static final Logger LOGGER = Logger.getLogger(SettingsScreenController.class.getName());

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label birthDateLabel;

    /**
     * Initializes the controller by setting user information in the labels.
     * If no user is logged in, the labels remain empty.
     */
    public void initialize() {
        User user = UserSession.getLoggedInUser();
        if (user != null) {
            LOGGER.info("Setting user info in settings screen for user: " + user.getUsername());
            usernameLabel.setText(user.getUsername());
            roleLabel.setText(user.getRole().toString());
            nameLabel.setText(user.getFirstName() + " " + user.getLastName());
            birthDateLabel.setText(user.getBirthDate() != null ? user.getBirthDate().toString() : "N/A");
        } else {
            LOGGER.warning("No user is logged in. User info labels not set.");
        }
    }
}
