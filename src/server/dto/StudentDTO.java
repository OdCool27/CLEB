package server.dto;

import server.model.User;

import java.time.LocalDateTime;

public class StudentDTO extends UserDTO{
    private String studentID;
    private String faculty;
    private String school;


    public StudentDTO(int userId, String firstName, String lastName, String email, User.Role userRole,
                      boolean active, LocalDateTime lastUpdated, String studentID, String faculty, String school) {
        super(userId, firstName, lastName, email, userRole, active, lastUpdated);
        this.studentID = studentID;
        this.faculty = faculty;
        this.school = school;
    }
}
