package server.dao;
import server.dto.StudentDTO;
import server.model.Student;
import server.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection conn;
    private UserDAO userDAO;

    public StudentDAO(Connection conn) {
        this.conn = conn;
        this.userDAO = new UserDAO(conn);
    }

    //INITIALIZES STUDENT TABLE
    public boolean initializeTable(){
        userDAO.initializeTable();//initializes user table first

        String sql = "CREATE TABLE IF NOT EXISTS Student (" +
                "userID INT PRIMARY KEY NOT NULL, " +
                "studentID VARCHAR(8) NOT NULL UNIQUE, " +
                "faculty VARCHAR(50) NOT NULL, " +
                "school VARCHAR(50) NOT NULL, " +
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

    //---------------------------CREATE OPERATION------------------------------------
    public boolean saveStudent(Student student){

        initializeTable();//Ensures all necessary tables are initialized

        if(userDAO.saveUser(student)) {//Saves user before saving student
            String sql = "INSERT INTO Student (userID, studentID, faculty, school) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, student.getUserID());
                pstmt.setString(2, student.getStudentID());
                pstmt.setString(3, student.getFaculty());
                pstmt.setString(4, student.getSchool());


                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;

            } catch (SQLException sqle) {
                System.err.println("Failed to save student: " + sqle.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //-------------------------------READ OPERATIONS----------------------------------------------

    //RETRIEVE INDIVIDUAL STUDENT USING STUDENT ID
    public Student getStudentByStudentId(String inputtedStudentID){
        String sql = "SELECT * " +
                "FROM `User` u " +
                "INNER JOIN Student s ON u.userID = s.userID " +
                "WHERE s.studentID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, inputtedStudentID);
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

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                return new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);
            }
        }catch(SQLException sqle){
            System.err.println("Failed to get Student: " + sqle.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //RETRIEVE INDIVIDUAL STUDENT USING EMAIL
    public Student getStudentByEmail(String inputtedEmail){
        String sql = "SELECT * " +
                "FROM `User` u " +
                "INNER JOIN Student s ON u.userID = s.userID " +
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

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                return new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);
            }
        }catch(SQLException sqle){
            System.err.println("Failed to get Student: " + sqle.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //RETRIEVE ALL STUDENTS
    public List<Student> getAllStudents(){
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM `User` u INNER JOIN Student s ON u.userID = s.userID";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
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

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                Student s = new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);

                students.add(s);
            }

            return students;

        }catch(SQLException sqle){
            System.err.println("Failed to get All Students: " + sqle.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }


    //-------------------------- UPDATE OPERATION-----------------------------------------------

    //UPDATE STUDENT AND USER TABLE
    public boolean updateStudent(Student student){
        boolean userUpdated = userDAO.updateUser(student);//Updates User table - May not yield any result based on what has been changed

        String sql = "UPDATE Student SET " +
                "faculty = ?, " +
                "school = ? " +
                "WHERE studentID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, student.getFaculty());
            pstmt.setString(2, student.getSchool());
            pstmt.setString(3, student.getStudentID());

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

    //----------------------------- DELETE OPERATIONS --------------------------------------------

    //DELETE A SINGLE STUDENT BY ID - No Need to call User Delete Method since the table has Casacade on delete
    public boolean deleteStudentById(String studentID){
        String sql = "DELETE FROM Student WHERE studentID = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, studentID);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        }catch(SQLException sqle){
            System.err.println("Failed to delete Student: " + sqle.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //DELETE ALL STUDENTS
    public boolean deleteAllStudents(){
        String sql = "DELETE FROM Student";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0;

        }catch(SQLException sqle){
            System.err.println("Failed to delete All Students: " + sqle.getMessage());
        }

        return false;
    }
}
