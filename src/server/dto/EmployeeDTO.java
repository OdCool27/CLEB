package server.dto;

import server.model.User;

import java.time.LocalDateTime;

public class EmployeeDTO extends UserDTO {


    public EmployeeDTO(int userId, String firstName, String lastName, String email, User.Role userRole, boolean active, LocalDateTime lastUpdated) {
        super(userId, firstName, lastName, email, userRole, active, lastUpdated);
    }
}
