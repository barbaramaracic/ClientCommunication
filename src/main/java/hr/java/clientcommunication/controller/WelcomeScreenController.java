package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.main.ClientCommunicationApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class WelcomeScreenController {

    //private WelcomeScreenController() {
    //} //da ima samo static metodu u sebi i ne moze se instancirati

    public static void showWelcomeScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreenController.class.getResource("/hr/java/clientcommunication/welcomeScreen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1065, 611);
        ClientCommunicationApplication.getMainStage().setTitle("Client Communication Preferences Tool");
        ClientCommunicationApplication.getMainStage().setScene(scene);
        ClientCommunicationApplication.getMainStage().show();
    }
}
