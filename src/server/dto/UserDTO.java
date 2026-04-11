package server.dto;

import server.model.User;import java.time.LocalDateTime;

public class UserDTO {
    private int userID;
    private String firstName;
    private String lastName;
    private String email;
    private User.Role role;
    private boolean isActive;
    private LocalDateTime lastUpdated;


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
}
