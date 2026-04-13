package dto;

import model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int userID;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected User.Role role;
    protected boolean isActive;
    protected LocalDateTime lastUpdated;
    protected String password;

    public UserDTO(int userId, String firstName, String lastName, String email,
                   User.Role userRole, boolean active, LocalDateTime lastUpdated) {
        this(userId, firstName, lastName, email, userRole, active, lastUpdated, null);
    }

    public UserDTO(int userId, String firstName, String lastName, String email,
                   User.Role userRole, boolean active, LocalDateTime lastUpdated, String password) {
        this.userID = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = userRole;
        this.isActive = active;
        this.lastUpdated = lastUpdated;
        this.password = password;
    }


    public UserDTO(User user){
        this.userID = user.getUserID();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.isActive = user.isActive();
        this.lastUpdated = user.getLastUpdated();
        this.password = null;
    }

    public int getUserID() { return userID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public User.Role getRole() { return role; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public String getPassword() { return password; }

}
