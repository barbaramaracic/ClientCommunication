package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.CommunicationLog;
import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.exception.UserFileNotFoundException;
import hr.java.clientcommunication.repository.UserRepository;
import hr.java.clientcommunication.utils.UserFileRepository;
import hr.java.clientcommunication.utils.UserFileRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchUsersController {

    private static final Logger logger = LoggerFactory.getLogger(SearchUsersController.class);

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> actionColumn;

    private final UserRepository userRepository = new UserRepository();
    private final UserFileRepository userFileRepo = new UserFileRepository();

    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void initialize() {
        // Configure columns
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        firstNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastName()));
        usernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole().name()));

        // Populate role filter
        roleComboBox.getItems().add(null); // Add "All" option
        roleComboBox.getItems().addAll(UserRole.values());

        setActionButtons();
        loadAllUsers();
    }

    private void loadAllUsers() {
        try {
            List<User> users = userFileRepo.findAllFromFile(); // You'll need to implement this
            userTableView.setItems(FXCollections.observableList(users));
        } catch (IOException e) {
            logger.error("Failed to load users", e);
            showError("Could not load users.");
        }
        catch (UserFileNotFoundException e) {
        logger.error("Could not find Users File", e);
        showError("Could not load users.");
    }
    }

    public void filterUsers() {
        try {
            List<User> users = userRepository.findAll();

            String firstName = firstNameTextField.getText().toLowerCase();
            String lastName = lastNameTextField.getText().toLowerCase();
            String username = usernameTextField.getText().toLowerCase();
            UserRole selectedRole = roleComboBox.getValue();

            List<User> filtered = users.stream()
                    .filter(u -> u.getFirstName().toLowerCase().contains(firstName))
                    .filter(u -> u.getLastName().toLowerCase().contains(lastName))
                    .filter(u -> u.getUsername().toLowerCase().contains(username))
                    .filter(u -> selectedRole == null || u.getRole() == selectedRole)
                    .collect(Collectors.toList());

            userTableView.setItems(FXCollections.observableList(filtered));

        } catch (IOException e) {
            logger.error("Failed to filter users", e);
            showError("Could not filter users.");
        }
    }

    private void setActionButtons() {
        if (!UserSession.isAdmin()) {
            actionColumn.setVisible(false);
            return;
        }

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = createDeleteButton();
            private final Button updateButton = createUpdateButton();
            private final HBox buttons = new HBox(5, updateButton, deleteButton);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }

            private Button createDeleteButton() {
                Button button = new Button("Delete");
                button.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                return button;
            }

            private Button createUpdateButton() {
                Button button = new Button("Update");
                button.setOnAction(e -> handleUpdate(getTableView().getItems().get(getIndex())));
                return button;
            }
        });
    }

    private void handleDelete(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete user?", ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirm Deletion");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userRepository.deleteById(user.getId()); // Implement this method
                    userTableView.getItems().remove(user);
                    showInfo("User deleted.");
                } catch (IOException e) {
                    showError("Failed to delete user.");
                    logger.error("Delete failed", e);
                }
            }
        });
    }

    private void handleUpdate(User user) {
        try {
            UpdateUserController controller = menuController.showUpdateUsersScreen(); // Implement
            controller.setUser(user);
        } catch (IOException e) {
            logger.error("Failed to open update screen", e);
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
