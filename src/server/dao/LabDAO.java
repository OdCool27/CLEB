package server.dao;

import server.model.Lab;
import server.model.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class LabDAO {
    private static final Logger logger = LogManager.getLogger(LabDAO.class);
    private Connection conn;

    public LabDAO(Connection conn) {
        this.conn = conn;
        logger.debug("LabDAO initialized with connection");
    }
    
    public boolean insertLab(Lab lab) {
        String query = "INSERT INTO labs (labID, name, roomName, building, floor, campus, numOfSeats) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            logger.debug("Inserting lab into database: {}", lab.getLabID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, lab.getLabID());
            pstmt.setString(2, lab.getName());
            pstmt.setString(3, lab.getLocation().getRoomName());
            pstmt.setString(4, lab.getLocation().getBuilding());
            pstmt.setInt(5, lab.getLocation().getFloor());
            pstmt.setString(6, lab.getLocation().getCampus());
            pstmt.setInt(7, lab.getNumOfSeats());
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab inserted successfully: {}", lab.getLabID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while inserting lab: {}", lab.getLabID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab: {}", lab.getLabID(), e);
            return false;
        }
    }
    

    public Lab getLabById(String labID) {
        String query = "SELECT * FROM labs WHERE labID = ?";
        
        try {
            logger.debug("Fetching lab from database with ID: {}", labID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, labID);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                logger.debug("Lab found in database: {}", labID);
                return mapResultSetToLab(rs);
            } else {
                logger.warn("Lab not found in database: {}", labID);
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab: {}", labID, e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab: {}", labID, e);
            return null;
        }
    }
    

    public List<Lab> getAllLabs() {
        String query = "SELECT * FROM labs";
        List<Lab> labs = new ArrayList<>();
        
        try {
            logger.debug("Fetching all labs from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Lab lab = mapResultSetToLab(rs);
                labs.add(lab);
            }
            
            logger.info("Retrieved {} labs from database", labs.size());
            return labs;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching all labs", e);
            return labs;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all labs", e);
            return labs;
        }
    }
    

    public boolean updateLab(Lab lab) {
        String query = "UPDATE labs SET name = ?, roomName = ?, building = ?, floor = ?, campus = ?, numOfSeats = ? WHERE labID = ?";
        
        try {
            logger.debug("Updating lab in database: {}", lab.getLabID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, lab.getName());
            pstmt.setString(2, lab.getLocation().getRoomName());
            pstmt.setString(3, lab.getLocation().getBuilding());
            pstmt.setInt(4, lab.getLocation().getFloor());
            pstmt.setString(5, lab.getLocation().getCampus());
            pstmt.setInt(6, lab.getNumOfSeats());
            pstmt.setString(7, lab.getLabID());
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab updated successfully: {}", lab.getLabID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while updating lab: {}", lab.getLabID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab: {}", lab.getLabID(), e);
            return false;
        }
    }
    

    public boolean deleteLab(String labID) {
        String query = "DELETE FROM labs WHERE labID = ?";
        
        try {
            logger.debug("Deleting lab from database: {}", labID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, labID);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab deleted successfully: {}", labID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while deleting lab: {}", labID, e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab: {}", labID, e);
            return false;
        }
    }
    
  
    private Lab mapResultSetToLab(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to Lab object");
        
        String labID = rs.getString("labID");
        String name = rs.getString("name");
        String roomName = rs.getString("roomName");
        String building = rs.getString("building");
        int floor = rs.getInt("floor");
        String campus = rs.getString("campus");
        int numOfSeats = rs.getInt("numOfSeats");
        
        Location location = new Location(roomName, building, floor, campus);
        return new Lab(labID, name, location, numOfSeats);
    }
}
