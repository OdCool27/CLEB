package server.model;

public abstract class User {
    protected int userID;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String passwordHash;
    protected Role role;
    protected boolean isActive;

    public enum Role {STUDENT, TECHNICIAN, ADMINISTRATOR}

    public User(){
        userID = -1;
        firstName = "";
        lastName = "";
        email = "";
        passwordHash = "";
        role = null;
        isActive = false;
    }

    public User(int userID,  String firstName, String lastName, String email, String passwordHash, Role role, boolean isActive ){
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String toString(){
        return "User Information\n====================\n" +
                "ID: " + userID + "\n" +
                "Full Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role.toString() + "\n" +
                "Active: " + isActive + "\n";
    }
}
