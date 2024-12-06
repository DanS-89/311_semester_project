package viewmodel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for the splash screen
 */
public class SpalshScreenController {

    @FXML
    private Label welcomeText;

    /**
     * Handles the onClick action event
     */
    @FXML
    protected void onButtonClick() {

        welcomeText.setText("Welcome to JavaFX Application!");
    }
}