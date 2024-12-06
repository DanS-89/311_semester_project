package service;

import java.util.prefs.Preferences;

/**
 * Class that ensures that ony one instance of a user session exists
 * Stores session details and privileges in a thread safe manner
 */
public class UserSession {

    //Single instance of UserSession, volatile to provide thread safety
    public static volatile UserSession instance;

    private String userName;
    private String password;
    private String privileges;
    private static final Object lock = new Object();
    private static final Preferences userPreferences = Preferences.userRoot().node("UserSession");

    /**
     * Constructor for a new UserSession with user details
     * @param userName Username of the user
     * @param password Password of the user
     * @param privileges Privileges of the user
     */
    public UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        userPreferences.put("USERNAME",userName);
        userPreferences.put("PASSWORD",password);
        userPreferences.put("PRIVILEGES",privileges);
    }

    /**
     * Gets a single instance of UserSession using a thread safe method
     * @param userName Username of the user
     * @param password Password of the user
     * @param privileges Privileges of the user
     * @return The single UserSession instance
     */
    public static UserSession getInstance(String userName, String password, String privileges) {
        if(instance == null) {
            synchronized (UserSession.class) {
                if(instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    /**
     * Gets the existing instance of the UserSession
     * @return The existing instance of the UserSession
     * @throws IllegalStateException if the UserSession has not been initialized
     */
    public static UserSession getInstance() {
        if(instance == null) {
            throw new IllegalStateException("UserSession not initialized yet");
        }
        return instance;
    }

    /**
     * Gets or initializes an instance of the UserSession
     * @param userName The username of the session
     * @param password The password of the session
     * @return Single instance of the UserSession
     */
    public static UserSession getInstance(String userName, String password) {
        if(instance == null) {
            instance = new UserSession(userName, password, "NONE");
        }
        return instance;
    }

    /**
     * Gets the username of the current session
     * @return Username associated with the session
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Gets the password of the current session
     * @return Password associated with the session
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the privileges of the current session
     * @return Privileges associated with the session
     */
    public String getPrivileges() {
        return this.privileges;
    }

    /**
     * Resets the attributes of the current session to blank or null
     */
    public void cleanUserSession() {
        this.userName = "";// or null
        this.password = "";
        this.privileges = "";// or null
    }

    /**
     * Generates a string representation of the UserSession object
     * @return String representation of the UserSession object
     */
    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }
}
