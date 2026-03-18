package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.Address;
import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.CommunicationPreference;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Controller responsible for creating and saving a new client.
 * Handles input validation, user confirmation, and saving to the database.
 */
public class CreateClientsController {

    private static final Logger logger = LoggerFactory.getLogger(CreateClientsController.class);

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField streetField;
    @FXML private TextField houseNumberField;
    @FXML private TextField cityField;
    @FXML private TextField postalCodeField;
    @FXML private CheckBox emailAllowedCheckBox;
    @FXML private CheckBox smsAllowedCheckBox;
    @FXML private CheckBox phoneCallAllowedCheckBox;

    /**
     * Called when the user clicks the Save button.
     * Prompts for confirmation, builds the Client object, and saves it to the database.
     */
    @FXML
    private void onSave() {
        if (!validateInput()) {
            return; // prekini ako validacija ne prođe
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to save this client?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    Address address = new Address.Builder()
                            .setStreet(streetField.getText())
                            .setHouseNumber(houseNumberField.getText())
                            .setCity(cityField.getText())
                            .setPostalCode(postalCodeField.getText())
                            .build();

                    CommunicationPreference preference = new CommunicationPreference();
                    preference.setEmailAllowed(emailAllowedCheckBox.isSelected());
                    preference.setSmsAllowed(smsAllowedCheckBox.isSelected());
                    preference.setPhoneCallAllowed(phoneCallAllowedCheckBox.isSelected());

                    Client client = new Client();
                    client.setFirstName(firstNameField.getText());
                    client.setLastName(lastNameField.getText());
                    client.setBirthDate(birthDatePicker.getValue());
                    client.setEmail(emailField.getText());
                    client.setPhone(phoneField.getText());
                    client.setAddress(address);
                    client.setPreference(preference);

                    ClientDatabaseRepository repository = new ClientDatabaseRepository();
                    repository.save(client);

                    logger.info("Client successfully saved: {}", client);
                    MenuController menuController = new MenuController();
                    menuController.showSearchClientsScreen();

                } catch (Exception e) {
                    logger.error("Error while saving client: {}", e.getMessage(), e);
                    showError("An error occurred while saving the client: " + e.getMessage());
                }
            } else {
                logger.info("User cancelled the client save operation.");
            }
        });
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String street = streetField.getText().trim();
        String houseNumber = houseNumberField.getText().trim();
        String city = cityField.getText().trim();
        String postalCode = postalCodeField.getText().trim();

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

        if (email.isEmpty() && phone.isEmpty()) {
            errors.append("- At least one contact method is required: email or phone.\n");
        }

        if (!email.isEmpty() && !isValidEmail(email)) {
            errors.append("- Email format is invalid.\n");
        }

        if (!phone.isEmpty() && !isValidPhone(phone)) {
            errors.append("- Phone number format is invalid.\n");
        }

        validateAddress(street, errors, houseNumber, city, postalCode);

        if (!emailAllowedCheckBox.isSelected() && !smsAllowedCheckBox.isSelected() && !phoneCallAllowedCheckBox.isSelected()) {
            errors.append("- At least one communication preference must be selected.\n");
        }

        if (errors.length() > 0) {
            showError("Please fix the following errors:\n" + errors.toString());
            return false;
        }

        return true;
    }

    private static void validateAddress(String street, StringBuilder errors, String houseNumber, String city, String postalCode) {
        if (street.isEmpty()) {
            errors.append("- Street is required.\n");
        }

        if (houseNumber.isEmpty()) {
            errors.append("- House number is required.\n");
        }

        if (city.isEmpty()) {
            errors.append("- City is required.\n");
        }

        if (postalCode.isEmpty()) {
            errors.append("- Postal code is required.\n");
        }
    }


    private boolean isValidEmail(String email) {
        // Jednostavan regex za email, možeš koristiti složeniji ako želiš
        return email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        // Brojevi, +, -, razmak dozvoljeni
        return phone.matches("^[+\\d][\\d\\s-]{5,}$");
    }

    /**
     * Displays an error dialog with a given message.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Client could not be saved");
        alert.showAndWait();
    }
}
