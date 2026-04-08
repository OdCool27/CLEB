package server.dao;

import server.model.User;
import server.util.LoggingUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
        LoggingUtil.debug(UserDAO.class, "UserDAO initialized with connection");
    }
    

    public boolean insertUser(User user) {
        String query = "INSERT INTO users (userID, firstName, lastName, email, passwordHash, role, isActive, lastUpdated) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            LoggingUtil.debug(UserDAO.class, "Inserting user into database: {}", user.getUserID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, user.getUserID());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPasswordHash());
            pstmt.setString(6, user.getRole().toString());
            pstmt.setBoolean(7, user.isActive());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(UserDAO.class, "User inserted successfully: {}", user.getUserID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(UserDAO.class, "SQLException while inserting user: {}", e, user.getUserID());
            return false;
        } catch (Exception e) {
            LoggingUtil.error(UserDAO.class, "Unexpected exception while inserting user: {}", e, user.getUserID());
            return false;
        }
    }
    
    /**
     * Retrieves a user by their ID from the database.
     * 
     * @param userId the user ID to search for
     * @return the User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE userID = ?";
        
        try {
            LoggingUtil.debug(UserDAO.class, "Fetching user from database with ID: {}", userId);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                LoggingUtil.debug(UserDAO.class, "User found in database: {}", userId);
                User user = mapResultSetToUser(rs);
                return user;
            } else {
                LoggingUtil.warn(UserDAO.class, "User not found in database: {}", userId);
                return null;
            }
            
        } catch (SQLException e) {
            LoggingUtil.error(UserDAO.class, "SQLException while fetching user: {}", e, userId);
            return null;
        } catch (Exception e) {
            LoggingUtil.error(UserDAO.class, "Unexpected exception while fetching user: {}", e, userId);
            return null;
        }
    }
    
  
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        
        try {
            LoggingUtil.debug(UserDAO.class, "Fetching all users from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
            
            LoggingUtil.info(UserDAO.class, "Retrieved {} users from database", users.size());
            return users;
            
        } catch (SQLException e) {
            LoggingUtil.error(UserDAO.class, "SQLException while fetching all users", e);
            return users;
        } catch (Exception e) {
            LoggingUtil.error(UserDAO.class, "Unexpected exception while fetching all users", e);
            return users;
        }
    }
    

    public boolean updateUser(User user) {
        String query = "UPDATE users SET firstName = ?, lastName = ?, email = ?, " +
                       "passwordHash = ?, role = ?, isActive = ?, lastUpdated = ? WHERE userID = ?";
        
        try {
            LoggingUtil.debug(UserDAO.class, "Updating user in database: {}", user.getUserID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPasswordHash());
            pstmt.setString(5, user.getRole().toString());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, user.getUserID());
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(UserDAO.class, "User updated successfully: {}", user.getUserID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(UserDAO.class, "SQLException while updating user: {}", e, user.getUserID());
            return false;
        } catch (Exception e) {
            LoggingUtil.error(UserDAO.class, "Unexpected exception while updating user: {}", e, user.getUserID());
            return false;
        }
    }
    

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE userID = ?";
        
        try {
            LoggingUtil.debug(UserDAO.class, "Deleting user from database: {}", userId);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            LoggingUtil.info(UserDAO.class, "User deleted successfully: {}", userId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LoggingUtil.error(UserDAO.class, "SQLException while deleting user: {}", e, userId);
            return false;
        } catch (Exception e) {
            LoggingUtil.error(UserDAO.class, "Unexpected exception while deleting user: {}", e, userId);
            return false;
        }
    }
    

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        LoggingUtil.debug(UserDAO.class, "Mapping ResultSet to User object");
        
        int userID = rs.getInt("userID");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        String passwordHash = rs.getString("passwordHash");
        User.Role role = User.Role.valueOf(rs.getString("role"));
        boolean isActive = rs.getBoolean("isActive");
        
        // Note: Create base User object - subclasses (Student, Employee) will be handled by respective DAOs
        return new User(userID, firstName, lastName, email, passwordHash, role, isActive) {};
    }
}
