package hr.java.clientcommunication.controller;
import hr.java.clientcommunication.entity.Address;
import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.CommunicationPreference;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class UpdateClientsController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdateClientsController.class);

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField houseNumberField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private CheckBox emailAllowedCheckBox;

    @FXML
    private CheckBox smsAllowedCheckBox;

    @FXML
    private CheckBox phoneCallAllowedCheckBox;

    private Client client;


    public void setClient(Client client) {
        this.client = client;

        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        birthDatePicker.setValue(client.getBirthDate());

        emailField.setText(client.getEmail());
        phoneField.setText(client.getPhone());

        if (client.getAddress() != null) {
            Address address = client.getAddress();
            streetField.setText(address.getStreet());
            houseNumberField.setText(address.getHouseNumber());
            cityField.setText(address.getCity());
            postalCodeField.setText(address.getPostalCode());
        }

        if (client.getPreference() != null) {
            CommunicationPreference pref = client.getPreference();
            emailAllowedCheckBox.setSelected(pref.isEmailAllowed());
            smsAllowedCheckBox.setSelected(pref.isSmsAllowed());
            phoneCallAllowedCheckBox.setSelected(pref.isPhoneCallAllowed());
        }
    }

    @FXML
    private void onSave() {
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

        if (!emailAllowedCheckBox.isSelected() && !smsAllowedCheckBox.isSelected() && !phoneCallAllowedCheckBox.isSelected()) {
            errors.append("- At least one communication preference must be selected.\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return;
        }

        // Ako je sve OK, nastavi sa spremanjem
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Update Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to update client data?");
        confirmationAlert.setContentText("Click OK to confirm or Cancel to go back.");

        if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                client.setFirstName(firstName);
                client.setLastName(lastName);
                client.setBirthDate(birthDate);
                client.setEmail(email);
                client.setPhone(phone);

                Address address = new Address.Builder()
                        .setStreet(street)
                        .setHouseNumber(houseNumber)
                        .setCity(city)
                        .setPostalCode(postalCode)
                        .build();
                client.setAddress(address);

                CommunicationPreference preference = new CommunicationPreference();
                preference.setEmailAllowed(emailAllowedCheckBox.isSelected());
                preference.setSmsAllowed(smsAllowedCheckBox.isSelected());
                preference.setPhoneCallAllowed(phoneCallAllowedCheckBox.isSelected());
                client.setPreference(preference);

                ClientDatabaseRepository repository = new ClientDatabaseRepository();
                repository.update(client);

                MenuController menuController = new MenuController();
                menuController.showSearchClientsScreen();
            } catch (Exception e) {
                logger.error("Failed to update client", e);
                showError("An error occurred while updating the client.");
            }
        }
    }

    // Pomoćne metode za validaciju maila i telefona (ako ih nema, napravi ih)
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\+?[0-9]{7,15}$"); // primjer jednostavne validacije broja telefona
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Update Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

}
