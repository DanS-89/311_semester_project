package viewmodel;
import com.azure.storage.blob.BlobClient;
import dao.StorageUploader;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import model.Major;
import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Controller class for the database GUI in the application
 * Responsible for managing user inputs and interacting with the database
 */
public class DB_GUI_Controller implements Initializable {

    //Objects needed for the db GUI controller
    private File file;
    StorageUploader store = new StorageUploader();
    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    Button editButton, deleteButton, addButton, clearButton;
    @FXML
    ComboBox<Major> majorComboBox;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem editItem, deleteItem, copyItem, clearItem;
    @FXML
    ProgressBar progressBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_email;
    @FXML
    private TableColumn<Person, Major> tv_major;

    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();


    //regex expressions for user input validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@farmingdale\\.edu$");
    //private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://[A-Za-z0-9.-]+\\.url$");

    /**
     * Initializes the GUI components and binds data to the table view
     * @param url The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            tv.setEditable(true);

            tv_fn.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_fn.setOnEditCommit(event -> {
                String newValue = event.getNewValue();
                if(validateInput(newValue, NAME_PATTERN.pattern())) {
                    Person person = event.getRowValue();
                    person.setFirstName(newValue);
                    tv.refresh();
                } else {
                    showValidationError("First name must contain only letters");
                    tv.refresh();
                }
            });

            tv_ln.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_ln.setOnEditCommit(event -> {
                String newValue = event.getNewValue();
                if(validateInput(newValue, NAME_PATTERN.pattern())) {
                    Person person = event.getRowValue();
                    person.setLastName(event.getNewValue());
                    tv.refresh();
                } else {
                    showValidationError("Last name must contain only letters");
                    tv.refresh();
                }
            });

            tv_department.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_department.setOnEditCommit(event -> {
                String newValue = event.getNewValue();
                if(validateInput(newValue, NAME_PATTERN.pattern())) {
                    Person person = event.getRowValue();
                    person.setDepartment(event.getNewValue());
                    tv.refresh();
                } else {
                    showValidationError("Department must contain only letters");
                    tv.refresh();
                }
            });

            tv_major.setCellFactory(ComboBoxTableCell.forTableColumn(
                    new StringConverter<Major>() {
                @Override
                public String toString(Major major){
                    return major == null ? "" : major.name();
                }
                @Override
                public Major fromString(String string){
                    return Major.valueOf(string);
                }
            },
            FXCollections.observableArrayList(Major.values())));
            tv_major.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setMajor(event.getNewValue());
                tv.refresh();
            });

            tv_email.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_email.setOnEditCommit(event -> {
                String newValue = event.getNewValue();
                if(validateInput(newValue, EMAIL_PATTERN.pattern())) {
                    Person person = event.getRowValue();
                    person.setEmail(event.getNewValue());
                    tv.refresh();
                } else {
                    showValidationError("Email must be valid farmingdale.edu address");
                    tv.refresh();
                }
            });


            tv.setOnKeyPressed(event -> {
                if(event.isControlDown()){
                    switch (event.getCode()) {
                        case E:
                            editRecord();
                            break;
                        case D:
                            deleteRecord();
                            break;
                        case R:
                            clearForm();
                            break;
                        case C:
                            copyRecord();
                            break;
                        default:
                            break;
                    }
                }
            });

            setupRowSelectionListener();

            editButton.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());

            deleteButton.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());

            addButton.disableProperty().bind(Bindings.createBooleanBinding(() -> tv.getSelectionModel().getSelectedItem() != null || !validateName(first_name.getText()) || !validateName(last_name.getText()) ||
                            !validateName(department.getText()) || !validateEmail(email.getText()) || majorComboBox.getSelectionModel().getSelectedItem() == null,
                            first_name.textProperty(), last_name.textProperty(), department.textProperty(), email.textProperty(), imageURL.textProperty(),
                            majorComboBox.getSelectionModel().selectedItemProperty(), tv.getSelectionModel().selectedItemProperty()
                )
            );

            clearButton.disableProperty().bind(first_name.textProperty().isEmpty().and(last_name.textProperty().isEmpty()).and(department.textProperty().isEmpty())
                    .and(majorComboBox.getSelectionModel().selectedItemProperty().isNull()).and(email.textProperty().isEmpty()).and(imageURL.textProperty().isEmpty()));

            editItem.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());

            deleteItem.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());

            copyItem.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());

            clearItem.disableProperty().bind(first_name.textProperty().isEmpty().and(last_name.textProperty().isEmpty()).and(department.textProperty().isEmpty())
                    .and(majorComboBox.getSelectionModel().selectedItemProperty().isNull()).and(email.textProperty().isEmpty()).and(imageURL.textProperty().isEmpty()));

            majorComboBox.setItems(FXCollections.observableArrayList(Major.values()));

            tv.setItems(data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new Person record to the database and updates the table view
     */
    @FXML
    protected void addNewRecord() {

        String emailInput = email.getText();

        if(isDuplicateEmail(emailInput)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate Email");
            alert.setHeaderText("Cannot add new record");
            alert.setContentText("An entry with this email already exists");
            alert.showAndWait();
            return;
        }

        try {
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    majorComboBox.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New Record");
            alert.setHeaderText("New record added");
            alert.setContentText("New record added successfully");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot add new record");
            alert.setContentText("There was an error while adding the record");
            alert.showAndWait();
        }
    }

    /**
     * Clears all input fields in the form and resets the text fields
     */
    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        majorComboBox.getSelectionModel().clearSelection();
        email.setText("");
        imageURL.setText("");

        tv.getSelectionModel().clearSelection();
        tv.requestFocus();
    }

    /**
     * Logs the user out and navigates to the login page
     * @param actionEvent Event triggered by clicking the corresponding button
     * @throws Exception if error occurs while loading resources
     */
    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the application
     */
    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    /**
     * Displays the About page
     */
    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Help page
     */
    @FXML
    protected void displayHelp() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/help.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Edits the selected record in the table view
     * Updates record with the changed data in the corresponding text fields
     */
    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        if(p == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No record selected");
            alert.setContentText("Please select a record to edit");
            alert.showAndWait();
            return;
        }
        if(!validateInput(first_name.getText(), NAME_PATTERN.pattern())){
            showValidationError("First must contain only letters");
            return;
        }
        if(!validateInput(last_name.getText(), NAME_PATTERN.pattern())){
            showValidationError("Last must contain only letters");
            return;
        }
        if(!validateInput(department.getText(), NAME_PATTERN.pattern())){
            showValidationError("Department must contain only letters");
            return;
        }
        if(!validateInput(email.getText(), EMAIL_PATTERN.pattern())){
            showValidationError("Email must be a valid farmingdale.edu address");
            return;
        }

        try {
            Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                    majorComboBox.getValue(), email.getText(), imageURL.getText());
            cnUtil.editUser(p.getId(), p2);
            data.remove(p);
            data.add(index, p2);
            tv.getSelectionModel().select(index);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edit Record");
            alert.setHeaderText("Edit record updated");
            alert.setContentText("Edit record updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot edit record");
            alert.setContentText("There was an error while editing the record");
            alert.showAndWait();
        }
    }

    /**
     * Deletes the selected record in the table view
     * Removes the record from the database and updates the table view
     */
    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);

        if(p == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No record selected");
            alert.setContentText("Please select a record to delete");
        }
        try {
            cnUtil.deleteRecord(p);
            data.remove(index);
            tv.getSelectionModel().select(index);
            clearForm();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete Record");
            alert.setHeaderText("Record deleted");
            alert.setContentText("record deleted successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot delete record");
            alert.setContentText("There was an error while deleting the record");
            alert.showAndWait();
        }
    }

    /**
     * Copies the selected record in the table view
     * Creates duplicates, so uses a unique id generator and adds '_copy' to email addresses
     * @throws Exception if error occurs during copying of record
     */
    @FXML
    protected void copyRecord() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();

        if(selectedPerson == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Record Selected");
            alert.setContentText("Please select a record to copy");
            alert.showAndWait();
            return;
        }
        int newId = generateUniqueID();
        String newEmail = generateUniqueEmail(selectedPerson.getEmail());

        Person copiedPerson = new Person(
                newId,
                selectedPerson.getFirstName(),
                selectedPerson.getLastName(),
                selectedPerson.getDepartment(),
                selectedPerson.getMajor(),
                newEmail,
                selectedPerson.getImageURL()
        );
        data.add(copiedPerson);

        try{
            cnUtil.insertUser(copiedPerson);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Copy Successful");
            alert.setHeaderText("Copy Successful");
            alert.setContentText("Record successfully copied");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Copy Failed");
            alert.setHeaderText("Copy Failed");
            alert.setContentText("An error occured");
            alert.showAndWait();
        }
    }

    /**
     * Generates a unique id for a new record
     * @return Unique id for the new record
     */
    protected int generateUniqueID() {
        int maxId = data.stream().mapToInt(Person::getId).max().orElse(0);
        return maxId + 1;
    }

    /**
     * Generates a unique email address based on an existing one in the database
     * @param email Original email to copy
     * @return a unique email address with '_copy' inserted into the email address
     */
    protected String generateUniqueEmail(String email){
        String emailPrefix = email.split("@")[0];
        String emailDomain = email.split("@")[1];
        return emailPrefix + "_copy@" + emailDomain;
    }

    /**
     * Displays an image selected by the user
     */
    /**
    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
            Task<Void> uploadTask = createUploadTask(file, progressBar);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            new Thread(uploadTask).start();
        }
    }
*/
    @FXML
    protected void showImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(img_view.getScene().getWindow());

        if(file != null){
            try {
                img_view.setImage(new Image(file.toURI().toString()));
                String blobName = UUID.randomUUID().toString() + "_" + file.getName();
                Task<Void> uploadTask = createUploadTask(file, blobName, progressBar);
                uploadTask.setOnSucceeded(event -> {
                    try {
                        StorageUploader storageUploader = new StorageUploader();
                        String imageURLString = storageUploader.getContainerClient().getBlobClient(blobName).getBlobUrl();
                        imageURL.setText(imageURLString);
                        Person selectedPerson = tv.getSelectionModel().getSelectedItem();
                        if(selectedPerson != null){
                            selectedPerson.setImageURL(imageURLString);
                            cnUtil.editUser(selectedPerson.getId(), selectedPerson);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Upload Image");
                            alert.setHeaderText("Image uploaded");
                            alert.setContentText("Image uploaded successfully");
                            alert.showAndWait();
                        }
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Cannot upload image");
                        alert.setContentText("An error occured while uploading the image");
                        alert.showAndWait();
                    }
                });
                new Thread(uploadTask).start();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot upload image");
                alert.setContentText("An error occured while uploading the image");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot upload image");
            alert.setContentText("An error occured while uploading the image");
            alert.showAndWait();
        }
    }

    /**
     * Uses the showSomeone() function
     *     @FXML
     *     protected void addUserRecord() {
     *         showSomeone();
     *     }
     */
   @FXML
    protected void addUserRecord() {
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User Account");
        dialog.setHeaderText("Please provide the following:");
        TextField userNameField = new TextField();
        userNameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        VBox dialogLayout = new VBox(10, new Label("Username"), userNameField, new Label("Password"), passwordField, new Label("Email"), emailField);
        dialogLayout.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(dialogLayout);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            String username = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            if(!validateInput(username, NAME_PATTERN.pattern()) && !validateInput(email, EMAIL_PATTERN.pattern())){
                showValidationError("Invalid input");
                return;
            }
            try {
                cnUtil.saveToDatabase(username, password, email);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Populates the form fields with data from the selected table view item
     * @param mouseEvent Event triggered by selecting an item in the table view
     */
    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        if(p == null){
            return;
        }
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        majorComboBox.setValue(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    /**
     * Switches to the applications light theme
     * @param actionEvent Event triggered by clicking on the light theme menu item
     */
    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches to the applications dark theme
     * @param actionEvent Event triggered by clicking on the dark theme menu item
     */
    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays dialogue to input information for a new user
     */
    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    /**
     * Class that represents the results of a user input
     */
    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }


    /**
     * Checks to see if an input email already exists in the database
     * @param email Email address to check
     * @return true if the email address already exists, false otherwise
     */
    private boolean isDuplicateEmail(String email) {
        for(Person person : data){
            if(person.getEmail().equalsIgnoreCase(email)){
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the input name with the regex pattern
     * @param name Name to validate
     * @return true if the name meets the regex pattern, false otherwise
     */
    private boolean validateName(String name){
        return NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates the input email with the regex pattern
     * @param email Email to validate
     * @return true if the email meets the regex pattern, false otherwise
     */
    private boolean validateEmail(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean validateInput(String input, String regex){
        return input != null && input.matches(regex);
    }

    private void showValidationError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Validates the input URL with the regex pattern
     * @param url URL to validate
     * @return true if the URL meets the regex pattern, false otherwise
     */
    /**private boolean validateURL(String url){
        return URL_PATTERN.matcher(url).matches();
    }
*/
    /**
     * Validates that a major is selected in the combo box, it is not null
     * @param selectedMajor Major selected in the comboBox
     * @return true if a major has been selected in the comboBox, false otherwise
     */
    private boolean validateMajorSelected(Object selectedMajor){
        return majorComboBox.getSelectionModel().getSelectedItem() != null;
    }

    /**
     * Imports user data from a CSV file
     */
    @FXML
    private void onImportCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV file");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if(file != null){
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                data.clear();

                while((line = reader.readLine()) != null){
                    String[] fields = line.split(",");
                    if(fields.length == 6){
                        Person person = new Person(fields[0], fields[1], fields[2], Major.valueOf(fields[3]), fields[4], fields[5]);

                        data.add(person);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Exports the current user data to a CSV file
     * Saves to a user specified location
     */
    @FXML
    private void onExportCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV file");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if(file != null){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for(Person person : data){
                    writer.write(String.join(",",
                            String.valueOf(person.getId()),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getDepartment(),
                            person.getMajor().toString(),
                            person.getEmail()));
                    writer.newLine();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a task to upload a file to a remote storage container
     * @param file File to upload
     * @param progressBar ProgressBar object to update during the upload
     * @return A Task<Void> which represents the upload
     */
    /**
    @FXML
    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100);
                    }
                }
                return null;
            }
        };
    }
*/
    @FXML
    private Task<Void> createUploadTask(File file,String blobName, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    StorageUploader storageUploader = new StorageUploader();
                    BlobClient blobClient = storageUploader.getContainerClient().getBlobClient(blobName);
                    long fileSize = Files.size(file.toPath());
                    long uploadedBytes = 0;

                    try (FileInputStream fileInputStream = new FileInputStream(file);
                         OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream(true)) {

                        byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            blobOutputStream.write(buffer, 0, bytesRead);
                            uploadedBytes += bytesRead;

                            // Calculate and update progress as a percentage
                            int progress = (int) ((double) uploadedBytes / fileSize * 100);
                            updateProgress(progress, 100);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private void setupRowSelectionListener(){
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                displaySelectedRecord(newValue);
            }
        });
    }

    private void displaySelectedRecord(Person selectedPerson) {
        try {
            first_name.setText(selectedPerson.getFirstName());
            last_name.setText(selectedPerson.getLastName());
            department.setText(selectedPerson.getDepartment());
            majorComboBox.setValue(selectedPerson.getMajor());
            email.setText(selectedPerson.getEmail());
            imageURL.setText(selectedPerson.getImageURL());

            String imageURLString = selectedPerson.getImageURL();
            if (imageURLString != null && !imageURLString.isEmpty()) {
                try {
                    img_view.setImage(new Image(imageURLString));
                } catch (Exception e) {
                    e.printStackTrace();
                    img_view.setImage(new Image(getClass().getResource("/images/profile.png").toExternalForm()));
                }
            } else {
                img_view.setImage(new Image(getClass().getResource("/images/profile.png").toExternalForm()));
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Display Error");
            alert.setContentText("Error displaying selected record: " + e.getMessage());
            alert.showAndWait();
            img_view.setImage(new Image(getClass().getResource("/images/profile.png").toExternalForm()));
        }
    }

    @FXML
    private void showMajorDistributionReport(){
        HashMap<String, Integer> majorCounts = new HashMap<>();
        for(Person person : data){
            String major = person.getMajor().toString();
            majorCounts.put(major, majorCounts.getOrDefault(major, 0) + 1);
        }
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Major");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Major Distribution");
        XYChart.Series series = new XYChart.Series();
        series.setName("Major Distribution");

        for(Map.Entry<String, Integer> entry : majorCounts.entrySet()){
            series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

        Stage stage = new Stage();
        stage.setTitle("Major Distribution Report");
        StackPane stackPane = new StackPane(barChart);
        Scene scene = new Scene(stackPane, 800, 600);
        stage.setScene(scene);
        Button printButton = new Button("Print");
        printButton.setOnAction(event ->
            printChart(barChart));
        VBox layout = new VBox(10, barChart, printButton);
        layout.setAlignment(Pos.CENTER);
        Scene printScene = new Scene(layout, 800, 600);
        stage.setScene(printScene);
        stage.show();
    }

    @FXML
    private void printChart(Node node){
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if(printerJob != null){
            boolean proceed = printerJob.showPrintDialog(null);
            if(proceed){
                boolean printed = printerJob.printPage(node);
                if(printed){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Print Successful");
                    alert.setHeaderText("Print Successful");
                    alert.setContentText("The report was successfully sent to the printer");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Print Failed");
                    alert.setHeaderText("Print Failed");
                    alert.setContentText("The report was not successfully sent to the printer. Please try again.");
                    alert.showAndWait();
                }
                printerJob.endJob();
            }
        }
    }
}
