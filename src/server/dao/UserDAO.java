package dao;

import dto.UserDTO;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    //INITIALIZES USER TABLE IN DATABASE
    public boolean initializeTable(){
        String sql = "CREATE TABLE IF NOT EXISTS `User` ("
                + "userID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                + "firstName VARCHAR(50) NOT NULL, "
                + "lastName VARCHAR(50) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE, "
                + "passwordHash VARCHAR(255) NOT NULL, "
                + "role VARCHAR(20) NOT NULL, "
                + "isActive BOOLEAN NOT NULL DEFAULT TRUE, "
                + "lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ");";

        try(PreparedStatement dbStmt = conn.prepareStatement(sql)){
            dbStmt.execute();
            return true;

        }catch(SQLException sqle){
            logger.error("Failed to initialize User Table", sqle);

        }catch(Exception e){
            logger.error("Unexpected error while initializing User table", e);
        }
        return false;
    }


    //---------------------------- CREATE METHOD--------------------------------------------------------

    //ADDS USER TO DATABASE - RETURNS USER ID
    public boolean saveUser(User user){
        String sql = "INSERT INTO `User` (firstName, lastName, email, passwordHash, role, isActive) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        //Allows for the retrieval of auto incremented IDs
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole().toString());
            stmt.setBoolean(6, user.isActive());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0){
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){

                        int generatedID = rs.getInt(1);
                        user.setUserID(generatedID);//Assigns userID to user
                    }
                    logger.info("User saved successfully: {}", user.getEmail());
                    return true;
                }
            }

        }catch(SQLException sqle){
            logger.error("Failed to save User: {}", user.getEmail(), sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while saving user", e);
        }
        return false;
    }


    //-----------------------------------------READ METHODS----------------------------------------

    //MAY NOT BE PERFORMED SINCE USER IS ABSTRACT

    //RETRIEVE USER BY EMAIL
    public UserDTO getUserByEmail(String inputtedEmail){
        String sql = "SELECT * FROM `User` WHERE email = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, inputtedEmail);

            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    int userId = rs.getInt("userID");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String email = rs.getString("email");
                    String passwordHash = rs.getString("passwordHash");
                    User.Role userRole = User.Role.valueOf(rs.getString("role"));
                    boolean active = rs.getBoolean("isActive");
                    LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                    logger.info("Retrieved user by email: {}", inputtedEmail);
                    return new UserDTO(userId, firstName, lastName, email, userRole, active, lastUpdated);
                }
            }

        }catch(SQLException sqle){
            logger.error("Failed to retrieve user by email: {}", inputtedEmail, sqle);

        } catch (Exception e) {
            logger.error("Unexpected error while retrieving user by email", e);

        }

        return null;
    }


    //RETRIEVE USER BY USER ID
    public UserDTO getUserById(int inputtedUserID){
        String sql = "SELECT * FROM `User` WHERE userID = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, inputtedUserID);

            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    int userId = rs.getInt("userID");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String email = rs.getString("email");
                    User.Role userRole = User.Role.valueOf(rs.getString("role"));
                    boolean active = rs.getBoolean("isActive");
                    LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();


                    logger.info("Retrieved user by userID: {}", inputtedUserID);
                    return new UserDTO(userId, firstName, lastName, email, userRole, active, lastUpdated);
                }
            }

        }catch(SQLException sqle){
            logger.error("Failed to retrieve user by userID: {}", inputtedUserID, sqle);

        } catch (Exception e) {
            logger.error("Unexpected error while retrieving user by ID", e);

        }

        return null;
    }

    public String getPasswordHashByUserId(int inputtedUserID) {
        String sql = "SELECT passwordHash FROM `User` WHERE userID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inputtedUserID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("passwordHash");
                }
            }
        } catch (SQLException sqle) {
            logger.error("Failed to retrieve password hash for userID: {}", inputtedUserID, sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving password hash", e);
        }

        return null;
    }


    //RETRIEVE ALL USERS
    public List<UserDTO> getAllUsers(){
        String sql = "SELECT * FROM `User`";
        List<UserDTO> users = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                int userId = rs.getInt("userID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                User.Role userRole = User.Role.valueOf(rs.getString("role"));
                boolean active = rs.getBoolean("isActive");
                LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();

                UserDTO userDTO = new UserDTO(userId, firstName, lastName, email, userRole, active, lastUpdated);
                users.add(userDTO);
            }

            logger.info("Retrieved all users, count: {}", users.size());
            return users;

        }catch(SQLException sqle){
            logger.error("Failed to retrieve users list", sqle);
        }

        return null;
    }

    //----------------------------------------UPDATE METHODS---------------------------------------
    //UPDATE USER IN DATABASE
    public boolean updateUser(User user){
        String sql = "UPDATE `User` SET " +
                "firstName = ?, " +
                "lastName = ?, " +
                "email = ?, " +
                "passwordHash = ?, " +
                "role = ?, " +
                "isActive = ? " +
                "WHERE userID = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole().toString());
            stmt.setBoolean(6, user.isActive());

            stmt.setInt(7, user.getUserID());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                logger.info("User updated successfully: {}", user.getEmail());
            } else {
                logger.warn("No user updated for userID: {}", user.getUserID());
            }

            return rowsUpdated > 0;

        }catch(SQLException sqle){
            logger.error("Failed to update User: {}", user.getEmail(), sqle);
        }

        return false;
    }


    //------------------------------------- DELETE METHOD --------------------------------------------
    //DELETES INDIVIDUAL USER
    public boolean deleteUser(int userID){
        String sql = "DELETE FROM `User` WHERE userID = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("User deleted successfully, userID: {}", userID);
            } else {
                logger.warn("No user found to delete with userID: {}", userID);
            }
            return rowsDeleted > 0;

        }catch(SQLException sqle){
            logger.error("Failed to delete User: {}", userID, sqle);
        }

        return false;
    }

    //DELETES ALL USERS
    public int deleteAllUser(){
        String sql = "DELETE FROM `User`";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            int rowsDeleted = stmt.executeUpdate(); //returns the number of deleted rows
            logger.info("Deleted all users, count: {}", rowsDeleted);
            return rowsDeleted;

        }catch (SQLException e) {
            logger.error("Failed to delete all users", e);

        }catch (Exception e) {
            logger.error("Unexpected error while deleting all users", e);

        }
        return 0;
    }

}
