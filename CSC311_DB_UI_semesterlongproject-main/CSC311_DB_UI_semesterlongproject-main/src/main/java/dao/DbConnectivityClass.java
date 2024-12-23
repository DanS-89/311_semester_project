package dao;
import model.Major;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;
import service.MyLogger;
import java.sql.*;

/**
 * Connection class to connect the application to the database
 */
public class DbConnectivityClass {

    final static String DB_NAME="csc311db";
    MyLogger lg= new MyLogger();
    final static String SQL_SERVER_URL = "jdbc:mysql://stevenscsc311server.mysql.database.azure.com";//update this server name
    final static String DB_URL = SQL_SERVER_URL +"/" + DB_NAME;//update this database name
    final static String USERNAME = "stevensadmin";// update this username
    final static String PASSWORD = "Farmingdale24@";// update this password
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    /**
     * Creates an observable list of Person's for the table view
     * @return data needed for each record in the database
     */
    public ObservableList<Person> getData() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    lg.makeLog("No data");
                }
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String majorStr = resultSet.getString("major");
                    String email = resultSet.getString("email");
                    String imageURL = resultSet.getString("imageURL");

                    Major major = null;
                    try{
                        major = Major.valueOf(majorStr);

                    } catch (IllegalArgumentException e) {
                        continue;
                    }

                    data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return data;
    }

    /**
     * Connects to database
     * @return boolean if there are registered users
     */
    public boolean connectToDatabase() {
            boolean hasRegistredUsers = false;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                //First, connect to MYSQL server and create the database if not created
                Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                Statement statement = conn.createStatement();
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+DB_NAME+"");
                statement.close();
                conn.close();

                //Second, connect to the database and create the table "users" if cot created
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                statement = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS users (" + "id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                        + "first_name VARCHAR(200) NOT NULL," + "last_name VARCHAR(200) NOT NULL,"
                        + "department VARCHAR(200),"
                        + "major VARCHAR(200),"
                        + "email VARCHAR(200) NOT NULL UNIQUE,"
                        + "imageURL TEXT)";
                statement.executeUpdate(sql);

                //check if we have users in the table users
                statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

                if (resultSet.next()) {
                    int numUsers = resultSet.getInt(1);
                    if (numUsers > 0) {
                        hasRegistredUsers = true;
                    }
                }

                statement.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return hasRegistredUsers;
        }

    /**
     * Method to query by last name
     * @param name last_name to be searched for in the database
     */
    public void queryUserByLastName(String name) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users WHERE last_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String major = resultSet.getString("major");
                    String department = resultSet.getString("department");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Major: " + major + ", Department: " + department);
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    /**
     * Method to list all users in the database
     */
    public void listAllUsers() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Department: " + department + ", Major: " + major + ", Email: " + email);
                }

                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    /**
     * Method to insert an object of type Person into the database
     * @param person object type to be added to the database
     */
    public void insertUser(Person person) throws SQLException {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "INSERT INTO users (first_name, last_name, department, major, email, imageURL) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, person.getFirstName());
                preparedStatement.setString(2, person.getLastName());
                preparedStatement.setString(3, person.getDepartment());
                preparedStatement.setString(4, person.getMajor().name());
                preparedStatement.setString(5, person.getEmail());
                preparedStatement.setString(6, person.getImageURL());
                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    lg.makeLog("A new user was inserted successfully.");
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    /**
     * Update an existing user in the database
     * @param id the unique id to be updated
     * @param p Person object with the updated data
     */
    public void editUser(int id, Person p) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getFirstName());
                preparedStatement.setString(2, p.getLastName());
                preparedStatement.setString(3, p.getDepartment());
                preparedStatement.setString(4, p.getMajor().name());
                preparedStatement.setString(5, p.getEmail());
                preparedStatement.setString(6, p.getImageURL());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * Method to delete a record from the database
     * @param person Person object to be deleted
     */
    public void deleteRecord(Person person) {
            int id = person.getId();
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "DELETE FROM users WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * Method to retrieve id from database where it is auto-incremented
     * @param p Person object that holds data
     * @return id of the person retrieved
     */
        public int retrieveId(Person p) {
            connectToDatabase();
            int id;
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT id FROM users WHERE email=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getEmail());

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                id = resultSet.getInt("id");
                preparedStatement.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            lg.makeLog(String.valueOf(id));
            return id;
        }

    /**
     * Method to save a new user to the database
     * @param username The username to be saved to the database
     * @param password The password to be saved to the database
     * @param email The email to be saved to the database
     * @throws SQLException If there is an issue connecting to the database or executing an insert query
     */
        public void saveToDatabase(String username, String password, String email) throws SQLException {
            if(!connectToDatabase()){
                throw new SQLException("Failed to connect to Database");
            }

            try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO userList (username, password, email) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to save user");
                }
            }
        }

    /**
     * Initializes a user table if one does not exist within the database
     */
    public void initializeUsersTable(){
            try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS userList ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "username VARCHAR(255) NOT NULL UNIQUE, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "email VARCHAR(255) NOT NULL UNIQUE)";

                statement.executeUpdate(createTableSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }