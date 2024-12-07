311 Semester Project

Farmingdale State College Records Application

This reposoitory contains the source code for a desktop
application built with JavaFX that helps manage student
records for Farmingdale State College. The app offers
functionalities like importing/exporting data, managing
student profiles, generating reports, and maintaining
a polished user interface with light and dark themes.

Table of Contents

1. Features
2. Code Structure
3. Key Functionalities
4. Technologies Used
5. Contact


Features:

Student Records Management: Add, edit, delete, and 
display records of students with attributes first
name, last name, department, major, Farmingdale
email address, and profile image.

CSV and Excel Integration:
Import records for .csv or .xls files.
Exporet data to .csv for backup or sharing.

Error Handling: Duplicates and invalid entries 
are skipped with appropriate notifications.

Dynamic Buttons: Buttons are enabled only when all 
requiredfields meet the necessary conditions, also
uses regex expressions for names and email
addresses.

Themes: Inlcudes both light and dark themes for 
an improved user experience improved user experience.


Light Theme:

![APP_SS1](https://github.com/user-attachments/assets/a1c6feb6-b2ed-4061-9b64-3776588a514e)


Dark Theme:

![APP_SS2](https://github.com/user-attachments/assets/533d8491-912f-4454-841f-3f5085205f5a)


Report Generation: Generates major distribution reports
as bar charts.
Inlcudes a one-page, landscape print functionality.

Help and About Sections: Detailed guidance and app information for users.


Code Structure:

Key Classes:
  1. DB_GUI_CONTROLLER: Handles all UI interactions and event handling.
  Implements mehtods for adding, editing, deleting, and importing/exporting records.
  Manages image upload and progress bar updates.
  Includes logic for report generation and printing.

  2. DbConnectivitiyClass: Manages database operationconnecting to (Microsoft Azure, creating tables, inserting data).
  Handles SQL queries and exception management for database interactions.

  3. StorageUploader: Han.dles image uplaods to a remote storage container.
  Supports progress bar updates during the upload process.

FXML Files:

  db_interface_gui.fxml: Main UI layout
  
  help.fxml: Help section layout
  
  about.fxml: About section layout
  
  login.fxml: Login section layout
  
  signUp.fxml: Sign up section layout
  
  splashscreen.fxml: Splash screen 
  

Key Functionalities:

1. Adding and Editing Records
   
    Logic: Fields must be validated before enabling buttons. 
  
    Code Snippet:
  
![APP_SS4](https://github.com/user-attachments/assets/fb144d3b-198a-4e34-a33f-be08888f628c)

3. CSV Import/Export
   
    Import: Parses .csv or .xls files.

    Handles exceptions and displays how many records are successfully added.

    Export: Saves the table view into a .csv file including all visible records.

    Code Snippet (Import):

  ![APP_SS5](https://github.com/user-attachments/assets/47c77826-6318-4c20-89a1-fa12e6ebe25e)

4. Image Upload with Progress Bar
   
    Logic: Upload images to a remote container using the createUploadTask() method.

    Updates a progress bar during the upload process.

    Code Snippet:
   
![APP_SS6](https://github.com/user-attachments/assets/05673422-1b18-4ba9-bc4d-e74216719a0e)

6. Report Generation
   
    Generates bar charts for displaying major distribution data.

    Supports printing the report on one page in landscape mode.

    Bar Chart:
   
![APP_SS7](https://github.com/user-attachments/assets/5c7b83cd-287d-4fe5-ab91-1885305643bc)


Technologies Used:

    JavaFX: For building the user interface.

    Microsoft Azure: Backend database for storing student records.

    Maven: Dependency and project build management.

    OpenCSV: For CSV parsing and writing.


Contact:

    Daniel Stevens

    stevdj@farmingdale.edu

    or

    stevens.j.daniel@gmail.com





