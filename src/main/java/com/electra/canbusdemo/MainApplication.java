package com.electra.canbusdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main entry point for the CANbusDEMO application.
 *
 * <p>
 * The {@code MainApplication} class extends {@code Application} and serves as the main class
 * to launch the JavaFX application. It initializes the main stage, loads the FXML file defining
 * the user interface, and sets up the event handler for the close request to gracefully exit the application.
 * </p>
 *
 * @see Application
 * @see Stage
 * @see FXMLLoader
 * @see Scene
 * @see javafx.fxml.FXMLLoader
 * @see javafx.scene.Scene
 */
public class MainApplication extends Application {

    public static Stage stage = null;

    @Override
    public void start(Stage stage) throws IOException {
        // Set an event handler for the close request to exit the application
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        // Store the main stage reference in the static variable for global access
        MainApplication.stage = stage;

        // Create a FXMLLoader to load the FXML file that defines the user interface
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainView.fxml"));

        // Load the FXML file to create the scene
        Scene scene = new Scene(fxmlLoader.load());

        // Set properties for the main stage
        stage.setTitle("CANbus DEMO");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch();
    }
}