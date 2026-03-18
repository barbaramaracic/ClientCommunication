package hr.java.clientcommunication.controller;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.main.ClientCommunicationApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

public class MenuController {
    private boolean denyIfNotAdmin() {
        if (!UserSession.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("Pristup dopušten samo administratorima.");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    public void showSearchClientsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/searchClientsScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        SearchClientsController controller = fxmlLoader.getController();
        controller.setMenuController(this);
        ClientCommunicationApplication.getMainStage().setTitle("Client Communication Preferences Tool");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showUserSettingsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/userSettingsScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        ClientCommunicationApplication.getMainStage().setTitle("User Settings");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public UpdateClientsController showUpdateClientsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/updateClientsScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Update Client");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();

        return fxmlLoader.getController();
    }

    public void showCreateClientsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/createClientsScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        ClientCommunicationApplication.getMainStage().setTitle("Create Client");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showChangesScreen() throws IOException {
        if (denyIfNotAdmin()) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/changesScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        ClientCommunicationApplication.getMainStage().setTitle("Changes");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showSearchCommunicationLogsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/searchCommunicationLogsScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        SearchCommunicationLogController controller = fxmlLoader.getController();
        controller.setMenuController(this); // <-- This line fixes the NullPointerException
        ClientCommunicationApplication.getMainStage().setTitle("Communication Logs");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showCreateCommunicationLogsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/createCommunicationLogScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);


        ClientCommunicationApplication.getMainStage().setTitle("Communication Logs");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public UpdateCommunicationLogController showUpdateCommunicationLogScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/updateCommunicationLogScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Update Client");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();

        return fxmlLoader.getController();
    }

    public void showLogInScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/loginScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Hello!");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showReportScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/reportScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Report");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public UpdateUserController showUpdateUsersScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/updateUsersScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Update User");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();

        return fxmlLoader.getController();
    }

    public void showSearchUsersScreen() throws IOException {
        if (denyIfNotAdmin()) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/searchUsersScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        SearchUsersController controller = fxmlLoader.getController();
        controller.setMenuController(this);

        ClientCommunicationApplication.getMainStage().setTitle("Search Users");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }

    public void showCreateUsersScreen() throws IOException {
        if (denyIfNotAdmin()) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/hr/java/clientcommunication/createUserScreen.fxml")); // ime FXML-a mora biti točno
        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);

        ClientCommunicationApplication.getMainStage().setTitle("Create User");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }
    public void logout() throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Logout Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/hr/java/clientcommunication/loginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
            ClientCommunicationApplication.getMainStage().setTitle("Hello");
            ClientCommunicationApplication.getMainStage().setScene(scene);
            ClientCommunicationApplication.getMainStage().show();
        }
    }
}