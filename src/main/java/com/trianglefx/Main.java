package com.trianglefx;

import com.trianglefx.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the TriangleFX JavaFX application.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application lifecycle.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 650, 600);
        
        primaryStage.setTitle("TriangleFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method, which launches the JavaFX runtime.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
