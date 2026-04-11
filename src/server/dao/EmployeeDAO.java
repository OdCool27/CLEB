package server.dao;

import server.model.Employee;
import server.model.User;
import server.util.LoggingUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EmployeeDAO {
    private Connection conn;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
        LoggingUtil.debug(EmployeeDAO.class, "EmployeeDAO initialized with connection");
    }
    

public boolean insertEmployee(Employee employee) {
    String query = "INSERT INTO employees (userID, empID, jobTitle, empPermissions) " +
                   "VALUES (?, ?, ?, ?)";
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Inserting employee into database: {}", employee.getEmpID());
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, employee.getUserID());
        pstmt.setString(2, employee.getEmpID());
        pstmt.setString(3, employee.getJobTitle());
        pstmt.setString(4, serializePermissions(employee.getPermissions()));
        
        int rowsAffected = pstmt.executeUpdate();
        LoggingUtil.info(EmployeeDAO.class, "Employee inserted successfully: {}", employee.getEmpID());
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while inserting employee: {}", e, employee.getEmpID());
        return false;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while inserting employee: {}", e, employee.getEmpID());
        return false;
    }
}
    

public Employee getEmployeeById(int userID) {
    String query = "SELECT * FROM employees WHERE userID = ?";
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Fetching employee from database with user ID: {}", userID);
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userID);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            LoggingUtil.debug(EmployeeDAO.class, "Employee found in database: {}", userID);
            return mapResultSetToEmployee(rs);
        } else {
            LoggingUtil.warn(EmployeeDAO.class, "Employee not found in database: {}", userID);
            return null;
        }
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while fetching employee: {}", e, userID);
        return null;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while fetching employee: {}", e, userID);
        return null;
    }
}
    

public Employee getEmployeeByEmpID(String empID) {
    String query = "SELECT * FROM employees WHERE empID = ?";
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Fetching employee from database with employee ID: {}", empID);
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, empID);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            LoggingUtil.debug(EmployeeDAO.class, "Employee found in database: {}", empID);
            return mapResultSetToEmployee(rs);
        } else {
            LoggingUtil.warn(EmployeeDAO.class, "Employee not found in database: {}", empID);
            return null;
        }
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while fetching employee: {}", e, empID);
        return null;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while fetching employee: {}", e, empID);
        return null;
    }
}
    
 List<Employee> getAllEmployees() {
    String query = "SELECT * FROM employees";
    List<Employee> employees = new ArrayList<>();
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Fetching all employees from database");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
            Employee employee = mapResultSetToEmployee(rs);
            employees.add(employee);
        }
        
        LoggingUtil.info(EmployeeDAO.class, "Retrieved {} employees from database", employees.size());
        return employees;
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while fetching all employees", e);
        return employees;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while fetching all employees", e);
        return employees;
    }
}
    

public boolean updateEmployee(Employee employee) {
    String query = "UPDATE employees SET empID = ?, jobTitle = ?, empPermissions = ? WHERE userID = ?";
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Updating employee in database: {}", employee.getEmpID());
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, employee.getEmpID());
        pstmt.setString(2, employee.getJobTitle());
        pstmt.setString(3, serializePermissions(employee.getPermissions()));
        pstmt.setInt(4, employee.getUserID());
        
        int rowsAffected = pstmt.executeUpdate();
        LoggingUtil.info(EmployeeDAO.class, "Employee updated successfully: {}", employee.getEmpID());
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while updating employee: {}", e, employee.getEmpID());
        return false;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while updating employee: {}", e, employee.getEmpID());
        return false;
    }
}
    
/**
 * Deletes an employee from the database.
 * 
 * @param userID the user ID of the employee to delete
 * @return true if deletion was successful, false otherwise
 */
public boolean deleteEmployee(int userID) {
    String query = "DELETE FROM employees WHERE userID = ?";
    
    try {
        LoggingUtil.debug(EmployeeDAO.class, "Deleting employee from database: {}", userID);
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userID);
        
        int rowsAffected = pstmt.executeUpdate();
        LoggingUtil.info(EmployeeDAO.class, "Employee deleted successfully: {}", userID);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        LoggingUtil.error(EmployeeDAO.class, "SQLException while deleting employee: {}", e, userID);
        return false;
    } catch (Exception e) {
        LoggingUtil.error(EmployeeDAO.class, "Unexpected exception while deleting employee: {}", e, userID);
        return false;
    }
}
    

private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
    LoggingUtil.debug(EmployeeDAO.class, "Mapping ResultSet to Employee object");
    
    int userID = rs.getInt("userID");
    String empID = rs.getString("empID");
    String jobTitle = rs.getString("jobTitle");
    Set<String> permissions = deserializePermissions(rs.getString("empPermissions"));
    
    // Note: User details should be fetched via UserDAO
    return new Employee(userID, "", "", "", "", User.Role.TECHNICIAN, true, empID, jobTitle, permissions);
}
    

    private String serializePermissions(Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        return String.join(",", permissions);
    }

    private Set<String> deserializePermissions(String permissionsStr) {
        Set<String> permissions = new HashSet<>();
        if (permissionsStr != null && !permissionsStr.isEmpty()) {
            String[] perms = permissionsStr.split(",");
            for (String perm : perms) {
                permissions.add(perm.trim());
            }
        }
        return permissions;
    }
}
