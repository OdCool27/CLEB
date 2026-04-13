package dao;

import dto.UserDTO;
import model.Student;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentDAO {
    private static final Logger logger = LogManager.getLogger(StudentDAO.class);
    private Connection conn;
    private UserDAO userDAO;

    public StudentDAO(Connection conn) {
        this.conn = conn;
        this.userDAO = new UserDAO(conn);
    }

    //INITIALIZES STUDENT TABLE
    public boolean initializeTable(){

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
            logger.error("Failed to initialize Student table", sqle);
        }catch(Exception e){
            logger.error("Unexpected error while initializing Student table", e);
        }

        return false;
    }

    //SAVES STUDENT RECORD TO DB
    public boolean saveStudent(Student student) {
        boolean userSaved = userDAO.saveUser(student);//SAVES USER FIRST

        if (!userSaved) {
            logger.error("User record was not saved, so student cannot be saved: {}", student.getStudentID());
            return false;
        }

        String sql = "INSERT INTO Student (userID, studentID, faculty, school) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getUserID());
            pstmt.setString(2, student.getStudentID());
            pstmt.setString(3, student.getFaculty());
            pstmt.setString(4, student.getSchool());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Student saved successfully: {}", student.getStudentID());
            }
            return rowsInserted > 0;

        } catch (SQLException sqle) {
            logger.error("Failed to save student: {}", student.getStudentID(), sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while saving student", e);
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
                boolean active = rs.getBoolean("isActive");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                logger.info("Retrieved student by ID: {}", inputtedStudentID);
                return new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);
            }
        }catch(SQLException sqle){
            logger.error("Failed to retrieve student by ID: {}", inputtedStudentID, sqle);
        }catch(Exception e){
            logger.error("Unexpected error while retrieving student by ID", e);
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
                boolean active = rs.getBoolean("isActive");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                logger.info("Retrieved student by email: {}", inputtedEmail);
                return new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);
            }
        }catch(SQLException sqle){
            logger.error("Failed to retrieve student by email: {}", inputtedEmail, sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving student by email", e);
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
                boolean active = rs.getBoolean("isActive");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                String studentID = rs.getString("studentID");
                String faculty = rs.getString("faculty");
                String school = rs.getString("school");

                Student s = new Student(userID, firstName, lastName, email, passwordHash,
                        userRole, active, lastUpdated, studentID, faculty, school);

                students.add(s);
            }

            logger.info("Retrieved all students, count: {}", students.size());
            return students;

        }catch(SQLException sqle){
            logger.error("Failed to retrieve all students", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving all students", e);
        }

        return students;
    }


    //-------------------------- UPDATE OPERATION-----------------------------------------------

    //UPDATE STUDENT AND USER TABLE
    public boolean updateStudent(Student student){
        Student existingStudent = getStudentByStudentId(student.getStudentID());
        UserDTO existingUser = userDAO.getUserById(student.getUserID());

        boolean userFieldsChanged = existingUser == null
                || !Objects.equals(student.getFirstName(), existingUser.getFirstName())
                || !Objects.equals(student.getLastName(), existingUser.getLastName())
                || !Objects.equals(student.getEmail(), existingUser.getEmail())
                || student.getRole() != existingUser.getRole()
                || student.isActive() != existingUser.isActive()
                || !Objects.equals(student.getPasswordHash(), userDAO.getPasswordHashByUserId(student.getUserID()));

        boolean studentFieldsChanged = existingStudent == null
                || !Objects.equals(student.getFaculty(), existingStudent.getFaculty())
                || !Objects.equals(student.getSchool(), existingStudent.getSchool());

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

            if (userFieldsChanged && !userUpdated) {
                logger.warn("Student update aborted because the shared User row was not updated for userID: {}", student.getUserID());
                return false;
            }

            if (studentFieldsChanged && !studentUpdated) {
                logger.warn("Student update aborted because the Student row was not updated for studentID: {}", student.getStudentID());
                return false;
            }

            if (studentUpdated || userUpdated || (!userFieldsChanged && !studentFieldsChanged)) {
                logger.info("Student updated successfully: {}", student.getStudentID());
            }

            return studentUpdated || userUpdated || (!userFieldsChanged && !studentFieldsChanged);

        }catch(SQLException sqle){
            logger.error("Failed to update Student: {}", student.getStudentID(), sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while updating student", e);
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
            if (rowsDeleted > 0) {
                logger.info("Student deleted successfully: {}", studentID);
            }
            return rowsDeleted > 0;

        }catch(SQLException sqle){
            logger.error("Failed to delete Student: {}", studentID, sqle);
        }catch (Exception e){
            logger.error("Unexpected error while deleting student", e);
        }

        return false;
    }

    //DELETE ALL STUDENTS
    public boolean deleteAllStudents(){
        String sql = "DELETE FROM Student";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            int rowsDeleted = pstmt.executeUpdate();
            logger.info("Deleted all students, count: {}", rowsDeleted);

            return rowsDeleted > 0;

        }catch(SQLException sqle){
            logger.error("Failed to delete All Students", sqle);
        }

        return false;
    }
}
