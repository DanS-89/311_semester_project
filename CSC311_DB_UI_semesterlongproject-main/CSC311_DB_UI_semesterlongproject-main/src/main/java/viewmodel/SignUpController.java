package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.prefs.Preferences;

/**
 * Controller class for sign-up page functionality in the application
 * Handles user input for account registration, validates the data provided, and saves to the database
 */
public class SignUpController {

    @FXML
    private Button newAccountButton;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerPasswordField;
    @FXML
    private TextField registerEmailField;

    private final DbConnectivityClass dbConnectivity = new DbConnectivityClass();
    private final Preferences userPreferences = Preferences.userRoot().node("UserSession");

    /**
     * Handles the action event to create a new account
     * @param actionEvent Event that is triggered by clicking the corresponding button
     */
    public void createNewAccount(ActionEvent actionEvent) {

        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        String email = registerEmailField.getText();

        if (validateInput(username, password, email)) {
            try {
                dbConnectivity.saveToDatabase(username, password, email);
                recordInPreferences(username, password);
                UserSession.getInstance(username, password, "USER");
                showAlert("Account created successfully", Alert.AlertType.INFORMATION);
                clearFields();
            } catch (SQLException e) {
                showAlert("Error saving user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Please fill in all fields", Alert.AlertType.WARNING);
        }
    }

    /**
     * Navigates back to the login page
     * @param actionEvent Event that is triggered by clicking the corresponding button
     */
    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates user input fields for the sign-up page
     * @param username Username entered
     * @param email Email entered
     * @param password Password entered
     * @return True is all fields are valid; false otherwise
     */
    private boolean validateInput(String username, String email, String password) {
        return !(username.isEmpty() || email.isEmpty() || password.isEmpty());
    }


    private void saveToDatabase(String username, String password, String email) {
        try {
            dbConnectivity.saveToDatabase(username, password, email);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error saving to database", Alert.AlertType.ERROR);
        }
    }

    /**
     * Stores the new account details into user preferences
     * @param username Username of new account
     * @param password Password of new account
     */
    private void recordInPreferences(String username, String password) {
        userPreferences.put("USERNAME", username);
        userPreferences.put("PASSWORD", password);
    }

    /**
     * Displays alert messages to the user
     * @param message Message to display in the alert
     * @param alertType Type of alert
     */
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears the text fields on the registration page
     */
    private void clearFields(){
        registerUsernameField.clear();
        registerPasswordField.clear();
        registerEmailField.clear();
    }
}
