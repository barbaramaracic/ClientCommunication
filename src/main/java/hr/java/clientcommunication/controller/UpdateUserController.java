package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.repository.UserRepository;
import hr.java.clientcommunication.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller responsible for updating existing user data.
 */
public class UpdateUserController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserController.class);

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordTextField;
    @FXML private PasswordField repeatPasswordTextField;
    @FXML private ComboBox<UserRole> roleComboBox;

    private final UserRepository userRepository = new UserRepository();
    private User userToEdit;

    /**
     * Initializes the controller with the user data to be edited.
     */
    public void setUser(User user) {
        this.userToEdit = user;
        loadUserData();
    }

    /**
     * Populates the fields with the user data.
     */
    private void loadUserData() {
        firstNameField.setText(userToEdit.getFirstName());
        lastNameField.setText(userToEdit.getLastName());
        birthDatePicker.setValue(userToEdit.getBirthDate());
        usernameField.setText(userToEdit.getUsername());
        usernameField.setDisable(true);  // Username should not be editable
        passwordTextField.clear();
        repeatPasswordTextField.clear();
        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.setValue(userToEdit.getRole());
    }

    @FXML
    public void onUpdate() {
        if (!validateInput()) {
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();
        String password = passwordTextField.getText();
        String repeatPassword = repeatPasswordTextField.getText();
        UserRole role = roleComboBox.getValue();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to update this user?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Update");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    userToEdit.setFirstName(firstName);
                    userToEdit.setLastName(lastName);
                    userToEdit.setBirthDate(birthDate);
                    userToEdit.setRole(role);

                    if (!password.isEmpty()) {
                        userToEdit.setHashpassword(PasswordUtil.hashPassword(password));
                    }

                    userRepository.update(userToEdit);

                    logger.info("User successfully updated: {}", userToEdit.getUsername());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User successfully updated!");

                    MenuController menuController = new MenuController();
                    menuController.showSearchUsersScreen();

                } catch (IOException e) {
                    logger.error("Failed to update user: {}", e.getMessage(), e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user: " + e.getMessage());
                }
            } else {
                logger.info("User update cancelled by user.");
            }
        });
    }

    /**
     * Validates the input fields.
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();
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

        if (!password.isEmpty() || !repeatPassword.isEmpty()) {
            if (!password.equals(repeatPassword)) {
                errors.append("- Passwords do not match.\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Errors", errors.toString());
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onBack() throws IOException {
        MenuController menuController = new MenuController();
        menuController.showSearchClientsScreen(); // ili kako god ti se zove metoda za povratak
    }
}
