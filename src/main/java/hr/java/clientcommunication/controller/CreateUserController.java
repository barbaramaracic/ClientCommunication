package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.exception.DuplicateUserException;
import hr.java.clientcommunication.repository.UserRepository;
import hr.java.clientcommunication.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller responsible for handling creation of a new user.
 * Includes user input validation, confirmation dialog, and saving user data.
 */
public class CreateUserController {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordTextField;
    @FXML private PasswordField repeatPasswordTextField;
    @FXML
    private ComboBox<UserRole> roleComboBox;
    @FXML private Button saveButton;

    private final UserRepository userRepository = new UserRepository();


    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values())); // ako koristiš enum
    }
        /**
         * Handles the save button click.
         * Validates input, asks for user confirmation, hashes password, and saves user to the repository.
         */
    @FXML
    public void onSave() {
        if (!validateInput()) {
            return;
        }
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();
        String username = usernameField.getText().trim();
        String password = passwordTextField.getText();
        String repeatPassword = repeatPasswordTextField.getText();
        UserRole selectedRole = roleComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || birthDate == null || username.isEmpty()
                || password.isEmpty() || repeatPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to save this user?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    User user = new User(null, firstName, lastName, birthDate, username, hashedPassword, selectedRole);

                    userRepository.save(user);

                    logger.info("User successfully saved: {}", username);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User successfully saved!");
                    clearFields();
                    MenuController menuController = new MenuController();
                    menuController.showSearchUsersScreen();

                } catch (IOException e) {
                    logger.error("Failed to save user: {}", e.getMessage(), e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user: " + e.getMessage());
                }

            } else {
                logger.info("User creation cancelled by user.");
            }
        });
    }

    /**
     * Navigates back to the login screen.
     *
     * @throws IOException if FXML loading fails
     */
    public void onBack() throws IOException {
        MenuController menuController = new MenuController();
        menuController.showLogInScreen();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();
        String username = usernameField.getText().trim();
        String password = passwordTextField.getText();
        String repeatPassword = repeatPasswordTextField.getText();

        if (firstName.isEmpty()) {
            errors.append("- First name is required.\n");
        }

        if (lastName.isEmpty()) {
            errors.append("- Last name is required.\n");
        }

        if (birthDate == null) {
            errors.append("- Birth date is required.\n");
        } else if (birthDate.isAfter(LocalDate.now())) {
            errors.append("- Birth date cannot be in the future.\n");
        }

        if (username.isEmpty()) {
            errors.append("- Username is required.\n");
        } else if (userRepository.existsByUsername(username)) {
            errors.append("- Username already exists.\n");
        }

        if (password.isEmpty()) {
            errors.append("- Password is required.\n");
        }

        if (repeatPassword.isEmpty()) {
            errors.append("- Repeat password is required.\n");
        }

        if (!password.isEmpty() && !repeatPassword.isEmpty() && !password.equals(repeatPassword)) {
            errors.append("- Passwords do not match.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Errors", errors.toString());
            return false;
        }

        return true;
    }


    /**
     * Displays an alert dialog with the given message.
     *
     * @param type    Alert type (INFO, ERROR, etc.)
     * @param title   Alert window title
     * @param message Message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        birthDatePicker.setValue(null);
        usernameField.clear();
        passwordTextField.clear();
        repeatPasswordTextField.clear();
    }


}
