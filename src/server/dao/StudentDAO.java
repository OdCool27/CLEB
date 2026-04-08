package server.dao;

import server.model.Student;
import server.model.User;
import server.util.LoggingUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class StudentDAO {
    private Connection conn;

    public StudentDAO(Connection conn) {
        this.conn = conn;
        LoggingUtil.debug(StudentDAO.class, "StudentDAO initialized with connection");
    }
    

    public boolean insertStudent(Student student) {
        String query = "INSERT INTO students (userID, studentID, faculty, school) " +
                       "VALUES (?, ?, ?, ?)";
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Inserting student into database: {}", student.getStudentID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, student.getUserID());
            pstmt.setString(2, student.getStudentID());
            pstmt.setString(3, student.getFaculty());
            pstmt.setString(4, student.getSchool());
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(StudentDAO.class, "Student inserted successfully: {}", student.getStudentID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while inserting student: {}", e, student.getStudentID());
            return false;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while inserting student: {}", e, student.getStudentID());
            return false;
        }
    }
    

    public Student getStudentById(int userID) {
        String query = "SELECT * FROM students WHERE userID = ?";
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Fetching student from database with user ID: {}", userID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userID);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                LoggingUtil.debug(StudentDAO.class, "Student found in database: {}", userID);
                return mapResultSetToStudent(rs);
            } else {
                LoggingUtil.warn(StudentDAO.class, "Student not found in database: {}", userID);
                return null;
            }
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while fetching student: {}", e, userID);
            return null;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while fetching student: {}", e, userID);
            return null;
        }
    }
    
  
    public Student getStudentByStudentID(String studentID) {
        String query = "SELECT * FROM students WHERE studentID = ?";
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Fetching student from database with student ID: {}", studentID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, studentID);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                LoggingUtil.debug(StudentDAO.class, "Student found in database: {}", studentID);
                return mapResultSetToStudent(rs);
            } else {
                LoggingUtil.warn(StudentDAO.class, "Student not found in database: {}", studentID);
                return null;
            }
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while fetching student: {}", e, studentID);
            return null;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while fetching student: {}", e, studentID);
            return null;
        }
    }
    
   
    public List<Student> getAllStudents() {
        String query = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Fetching all students from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                students.add(student);
            }
            
            LoggingUtil.info(StudentDAO.class, "Retrieved {} students from database", students.size());
            return students;
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while fetching all students", e);
            return students;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while fetching all students", e);
            return students;
        }
    }
    

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET studentID = ?, faculty = ?, school = ? WHERE userID = ?";
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Updating student in database: {}", student.getStudentID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, student.getStudentID());
            pstmt.setString(2, student.getFaculty());
            pstmt.setString(3, student.getSchool());
            pstmt.setInt(4, student.getUserID());
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(StudentDAO.class, "Student updated successfully: {}", student.getStudentID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while updating student: {}", e, student.getStudentID());
            return false;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while updating student: {}", e, student.getStudentID());
            return false;
        }
    }
    

    public boolean deleteStudent(int userID) {
        String query = "DELETE FROM students WHERE userID = ?";
        
        try {
            LoggingUtil.debug(StudentDAO.class, "Deleting student from database: {}", userID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userID);
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(StudentDAO.class, "Student deleted successfully: {}", userID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(StudentDAO.class, "SQLException while deleting student: {}", e, userID);
            return false;
        } catch (Exception e) {
            LoggingUtil.error(StudentDAO.class, "Unexpected exception while deleting student: {}", e, userID);
            return false;
        }
    }
    

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        LoggingUtil.debug(StudentDAO.class, "Mapping ResultSet to Student object");
        
        int userID = rs.getInt("userID");
        String studentID = rs.getString("studentID");
        String faculty = rs.getString("faculty");
        String school = rs.getString("school");
        

        return new Student(userID, "", "", "", "", User.Role.STUDENT, true, studentID, faculty, school);
    }
}
