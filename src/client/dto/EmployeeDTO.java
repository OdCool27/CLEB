package dto;

import model.Employee;
import model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmployeeDTO extends UserDTO implements Serializable {
    private String empID;
    private String jobTitle;

    public EmployeeDTO(int userId, String firstName, String lastName, String email, User.Role userRole,
                       boolean active, LocalDateTime lastUpdated, String empID, String jobTitle) {
        this(userId, firstName, lastName, email, userRole, active, lastUpdated, null, empID, jobTitle);
    }

    public EmployeeDTO(int userId, String firstName, String lastName, String email, User.Role userRole,
                       boolean active, LocalDateTime lastUpdated, String password, String empID, String jobTitle) {
        super(userId, firstName, lastName, email, userRole, active, lastUpdated, password);
        this.empID = empID;
        this.jobTitle = jobTitle;
    }

    public EmployeeDTO(Employee employee) {
        super(employee);
        this.empID = employee.getEmpID();
        this.jobTitle = employee.getJobTitle();
    }

    public String getEmpID() {
        return empID;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}
