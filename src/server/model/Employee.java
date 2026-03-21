package server.model;

import java.util.HashSet;
import java.util.Set;

public class Employee extends User {
    private String empID;
    private Set<String> permissions;

    public Employee() {
        super();
        empID = "";
        permissions = new HashSet<>();
    }

    public Employee(int userID, String firstName, String lastName, String email,
                    String passwordHash, Role role, boolean isActivated,
                    String empID, Set<String> permissions) {
        super(userID, firstName, lastName, email, passwordHash, role, isActivated);
        this.empID = empID;
        this.permissions = permissions;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermission(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Employee Information\n====================\n" +
                "Employee ID: " + empID + "\n" +
                "Full Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role + "\n" +
                "Permissions: " + permissions + "\n" +
                "Active: " + isActive + "\n";
    }
}