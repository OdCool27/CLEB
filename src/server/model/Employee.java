package server.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Employee extends User {
    private String empID;
    private String jobTitle;

    public Employee() {
        super();
        empID = "";
        jobTitle = "";
    }

    public Employee(int userID, String firstName, String lastName, String email,
                    String passwordHash, Role role, boolean isActivated, LocalDateTime lastUpdated,
                    String empID, String jobTitle) {
        super(userID, firstName, lastName, email, passwordHash, role, isActivated, lastUpdated);
        this.empID = empID;
        this.jobTitle = jobTitle;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }


    @Override
    public String toString() {
        return "Employee Information\n====================\n" +
                "Employee ID: " + empID + "\n" +
                "Full Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role + "\n" +
                "Active: " + isActive + "\n";
    }
}