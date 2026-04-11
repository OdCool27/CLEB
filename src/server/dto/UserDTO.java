package server.dto;

import server.model.User;
import java.time.LocalDateTime;
import java.io.Serializable;

public class UserDTO implements Serializable {
    protected int userID;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected User.Role role;
    protected boolean isActive;
    protected LocalDateTime lastUpdated;

    public UserDTO(int userId, String firstName, String lastName, String email,
                   User.Role userRole, boolean active, LocalDateTime lastUpdated) {
        this.userID = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = userRole;
        this.isActive = active;
        this.lastUpdated = lastUpdated;
    }

    public int getUserID() { return userID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public User.Role getRole() { return role; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}
