package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.main.ClientCommunicationApplication;
import hr.java.clientcommunication.utils.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller for the login screen.
 * Handles user authentication and navigation.
 */
public class LoginScreenController {

    private static final Logger logger = LoggerFactory.getLogger(LoginScreenController.class);
    private static final String USERS_FILE = "dat/users.txt";

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    /**
     * Handles login attempt when the login button is pressed.
     *
     * @param event the button click event
     * @throws IOException if screen transition fails
     */
    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            Optional<User> matchedUser = loadUsers().stream()
                    .filter(user -> user.getUsername().equals(username)
                            && user.getHashpassword().equals(PasswordUtil.hashPassword(password)))
                    .findFirst();

            if (matchedUser.isPresent()) {
                User user = matchedUser.get();
                UserSession.setLoggedInUser(user);

                logger.info("User '{}' logged in successfully with role '{}'", user.getUsername(), user.getRole());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + user.getUsername() + "!\nRole: " + user.getRole());
                alert.showAndWait();

                WelcomeScreenController.showWelcomeScreen();
            } else {
                errorLabel.setText("Incorrect username or password.");
                logger.warn("Login failed for username '{}'", username);
            }
        } catch (Exception e) {
            logger.error("Login process failed: {}", e.getMessage(), e);
            errorLabel.setText("An error occurred during login.");
        }
    }

    /**
     * Loads users from the users.txt file.
     *
     * @return list of users
     * @throws IOException if reading file fails
     */
    private List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(";");
                    if (parts.length != 7) {
                        logger.warn("Skipping malformed user entry: {}", line);
                        continue;
                    }

                    User user = new User(
                            Long.parseLong(parts[0].trim()),
                            parts[1],
                            parts[2],
                            LocalDate.parse(parts[3]),
                            parts[4],
                            parts[5],
                            UserRole.valueOf(parts[6].toUpperCase())
                    );
                    users.add(user);
                } catch (Exception parseEx) {
                    logger.error("Failed to parse user line: '{}'. Error: {}", line, parseEx.getMessage(), parseEx);
                }
            }
        } catch (IOException ioEx) {
            logger.error("Error reading users file: {}", ioEx.getMessage(), ioEx);
        }

        return users;
    }

    /**
     * Navigates to the user signup screen.
     *
     * @throws IOException if FXML loading fails
     */
    public void showSignUpScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/userSignUpScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Sign Up");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }
}
