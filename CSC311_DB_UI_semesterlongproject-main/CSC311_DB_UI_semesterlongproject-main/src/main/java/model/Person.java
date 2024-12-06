package model;

/**
 * Represents a person with various attributes name, department, major, email, and URL
 * Stores and manages data for records
 */
public class Person {

    private Integer id;
    private String firstName;
    private String lastName;
    private String department;
    private Major major;
    private String email;
    private String imageURL;

    /**
     * Default constructor for the Person class, initializes with default values
     */
    public Person() {
    }

    /**
     * Constructor for creating a Person object
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param department Person's department
     * @param major Person's major
     * @param email Person's email
     * @param imageURL Person's imageURL
     */
    public Person(String firstName, String lastName, String department, Major major, String email,  String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.email = email;
        this.imageURL = imageURL;
    }

    /**
     * Constructor for creating a Person object, includes a unique id number
     * @param id Person's unique id
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param department Person's department
     * @param major Person's major
     * @param email Person's email
     * @param imageURL Person's imageURL
     */
    public Person(Integer id, String firstName, String lastName, String department, Major major, String email,  String imageURL) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.email = email;
        this.imageURL = imageURL;
    }

    /**
     * Gets the Peron's email address
     * @return Person's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the Person's email address
     * @param email Person's email address to be updated to
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the Peron's unique id
     * @return Person's unique id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Updates the Person's unique id
     * @param id Person's unique id to be updated to
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the Peron's first name
     * @return Persons first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the Person's first name
     * @param firstName Person's first name to be updated to
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the Person's last name
     * @return Person's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the Person's last name
     * @param lastName Person's last name to be updated to
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the Person's major
     * @return Person's major
     */
    public Major getMajor() {
        return major;
    }

    /**
     * Updates the Person's major
     * @param major Person's major to be updated to
     */
    public void setMajor(Major major) {
        this.major = major;
    }

    /**
     * Gets the Peron's department
     * @return Person's department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Updates the Person's department
     * @param department Person's department to be updated to
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Gets the Person's imageURL
     * @return Person's imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Updates the Person's imageURL
     * @param imageURL Person's imageURL to be updated to
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Generates a string of the Person object and their attributes
     * @return A string representation of the Person object
     */
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}