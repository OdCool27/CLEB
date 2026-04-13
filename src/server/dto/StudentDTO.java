package dto;

import model.Student;
import model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StudentDTO extends UserDTO implements Serializable {
    private String studentID;
    private String faculty;
    private String school;


    public StudentDTO(int userId, String firstName, String lastName, String email, User.Role userRole,
                      boolean active, LocalDateTime lastUpdated, String studentID, String faculty, String school) {
        this(userId, firstName, lastName, email, userRole, active, lastUpdated, null, studentID, faculty, school);
    }

    public StudentDTO(int userId, String firstName, String lastName, String email, User.Role userRole,
                      boolean active, LocalDateTime lastUpdated, String password, String studentID, String faculty, String school) {
        super(userId, firstName, lastName, email, userRole, active, lastUpdated, password);
        this.studentID = studentID;
        this.faculty = faculty;
        this.school = school;
    }

    public StudentDTO(Student student) {
        super(student);
        this.studentID = student.getStudentID();
        this.faculty = student.getFaculty();
        this.school = student.getSchool();
    }

    public String getStudentID() { return studentID; }
    public String getFaculty() { return faculty; }
    public String getSchool() { return school; }

}
