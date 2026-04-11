package server.dao;

import server.model.Employee;
import server.model.User;
import server.util.LoggingUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EmployeeDAO {
    private Connection conn;
    private UserDAO userDAO;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
        this.userDAO = new UserDAO(conn);
    }


    //INITIALIZES STUDENT TABLE
    public boolean initializeTable(){
        userDAO.initializeTable();//initializes user table first

        String sql = "CREATE TABLE IF NOT EXISTS Employee (" +
                "userID INT PRIMARY KEY NOT NULL, " +
                "empID VARCHAR(6) NOT NULL UNIQUE, " +
                "jobTitle VARCHAR(50) NOT NULL, " +
                "FOREIGN KEY (userID) REFERENCES `User`(userID) ON DELETE CASCADE " +
                ");";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.execute();
            return true;

        }catch(SQLException sqle){
            System.err.println("Failed to initialize table: "+ sqle.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }


    //-------------------------------------- CREATE OPERATION--------------------------------------

    //CREATES EMPLOYEE RECORD
    public boolean saveEmployee(Employee employee){
        initializeTable(); //Ensures that User table is created.

        if(userDAO.saveUser(employee)){
            String sql = "INSERT INTO Employee (userID, empID, jobTitle) VALUES (?, ?, ?)";

            try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setInt(1, employee.getUserID());
                pstmt.setString(2, employee.getEmpID());
                pstmt.setString(3, employee.getJobTitle());

                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;

            }catch(SQLException sqle){
                System.err.println("Failed to save employee: "+ sqle.getMessage());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    //------------------------------------------ RETRIEVE OPERATION ------------------------------------

    //RETRIEVES EMPLOYEE RECORD BY EMPLOYEE ID
    public Employee getEmployeeById(String inputtedEmpID){
        String sql = "SELECT * " +
                "FROM `User` u " +
                "INNER JOIN Emploee emp ON u.userID = emp.userID " +
                "WHERE emp.empID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, inputtedEmpID);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int userID = rs.getInt("userID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String passwordHash = rs.getString("passwordHash");
                User.Role userRole = User.Role.valueOf(rs.getString("role"));
                boolean active = rs.getBoolean("active");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String empID = rs.getString("empID");
                String jobTitle = rs.getString("jobTitle");

                return new Employee(userID, firstName, lastName, email, passwordHash, userRole, active, lastUpdated,
                        empID, jobTitle);
            }

        }catch(SQLException sqle){
            System.err.println("Failed to get employee by ID: "+ sqle.getMessage());
        }

        return null;
    }


    //RETRIEVES EMPLOYEE RECORD BY EMAIL
    public Employee getEmployeeByEmail(String inputtedEmail){
        String sql = "SELECT * " +
                "FROM `User` u " +
                "INNER JOIN Emploee emp ON u.userID = emp.userID " +
                "WHERE u.email = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, inputtedEmail);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int userID = rs.getInt("userID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String passwordHash = rs.getString("passwordHash");
                User.Role userRole = User.Role.valueOf(rs.getString("role"));
                boolean active = rs.getBoolean("active");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String empID = rs.getString("empID");
                String jobTitle = rs.getString("jobTitle");

                return new Employee(userID, firstName, lastName, email, passwordHash, userRole, active, lastUpdated,
                        empID, jobTitle);
            }

        }catch(SQLException sqle){
            System.err.println("Failed to get employee by Email: "+ sqle.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    //RETRIEVE ALL EMPLOYEES
    public List<Employee> getAllEmployees(){
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM `User` u INNER JOIN Employee emp on u.userID = emp.userID";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userID = rs.getInt("userID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String passwordHash = rs.getString("passwordHash");
                User.Role userRole = User.Role.valueOf(rs.getString("role"));
                boolean active = rs.getBoolean("active");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String empID = rs.getString("empID");
                String jobTitle = rs.getString("jobTitle");

                Employee emp = new Employee(userID, firstName, lastName, email, passwordHash, userRole, active, lastUpdated,
                        empID, jobTitle);

                employees.add(emp);
            }

            return employees;

        }catch(SQLException sqle){
            System.err.println("Failed to get employee by ID: "+ sqle.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    //------------------------------------------- UPDATE OPERATIONS ----------------------------------------

    //UPDATES EMPLOYEE RECORD
    public boolean updateEmployee(Employee employee){
        boolean userUpdated = userDAO.updateUser(employee);

        String sql = "UPDATE Employee SET " +
                "jobTitle = ?, " +
                "WHERE empID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, employee.getJobTitle());
            pstmt.setString(2, employee.getEmpID());

            int rowsUpdated = pstmt.executeUpdate();
            boolean studentUpdated = rowsUpdated > 0;

            return studentUpdated || userUpdated; //IF SOMETHING IS UPDATED THEN RETURN TRUE

        }catch(SQLException sqle){
            System.err.println("Failed to update Student: " + sqle.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    //------------------------------------------- DELETE OPERATIONS ----------------------------------------

    //DELETE A SINGLE Employee BY ID - No Need to call User Delete Method since the table has Casacade on delete
    public boolean deleteEmployeeById(String empID){
        String sql = "DELETE FROM Employee WHERE empID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, empID);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        }catch(SQLException sqle){
            System.err.println("Failed to delete Employee: " + sqle.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //DELETE ALL EMPLOYEES
    public boolean deleteAllEmployees(){
        String sql = "DELETE FROM Employee";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0;

        }catch(SQLException sqle){
            System.err.println("Failed to delete All Employees: " + sqle.getMessage());
        }

        return false;
    }

}
