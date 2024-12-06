package viewmodel;

import dao.DbConnectivityClass;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The Main Application class that extends the Application class
 * Entry point for the application
 * Initializes resources, sets up primary stage, and launches GUI
 */
public class MainApplication extends Application {

    private static Scene scene;
    private static DbConnectivityClass cnUtil;
    private Stage primaryStage;

    /**
     * Main method that is the entry point for the application
     * Initializes the database connection and launches application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        cnUtil = new DbConnectivityClass();
        cnUtil.initializeUsersTable();
        launch(args);
    }

    /**
     * start method is called after the application launches and displays first scene
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception if an error occurs upon launch
     */
    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image(getClass().getResourceAsStream("/images/DollarClouddatabase.png"));
        this.primaryStage = primaryStage;
        this.primaryStage.setResizable(false);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("FSC CSC311 _ Database Project");
        showScene1();
    }

    /**
     * Loads, initializes, and displays the first scene
     * @throws Exception if an error occurs while loading
     */
    private void showScene1() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/splashscreen.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            changeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the current scene with a fade transition
     * Method that loads a new fxml layout and style sheets
     * Used to transition between scenes
     * @throws Exception if an error occurs while loading
     */
    public void changeScene() {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource("/view/login.fxml").toURI().toURL());
            Scene currentScene = primaryStage.getScene();
            Parent currentRoot = currentScene.getRoot();
            currentScene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), currentRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                Scene newScene = new Scene(newRoot, 900, 600);
                primaryStage.setScene(newScene);
                primaryStage.show();
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}