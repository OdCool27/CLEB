package server.dao;

import server.model.LabSeat;
import server.model.Lab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class LabSeatDAO {
    private static final Logger logger = LogManager.getLogger(LabSeatDAO.class);
    private Connection conn;

    public LabSeatDAO(Connection conn) {
        this.conn = conn;
        logger.debug("LabSeatDAO initialized with connection");
    }
    

    public boolean insertLabSeat(LabSeat labSeat, String seatCode) {
        String query = "INSERT INTO labSeats (seatID, labID, seatCode) VALUES (?, ?, ?)";
        
        try {
            logger.debug("Inserting lab seat into database: {}", labSeat.getSeatID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, String.valueOf(labSeat.getSeatID()));
            pstmt.setString(2, labSeat.getSeatLocation().getLabID());
            pstmt.setString(3, seatCode);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat inserted successfully: {}", labSeat.getSeatID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while inserting lab seat: {}", labSeat.getSeatID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab seat: {}", labSeat.getSeatID(), e);
            return false;
        }
    }
    

    public LabSeat getLabSeatById(String seatID) {
        String query = "SELECT * FROM labSeats WHERE seatID = ?";
        
        try {
            logger.debug("Fetching lab seat from database with ID: {}", seatID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, seatID);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                logger.debug("Lab seat found in database: {}", seatID);
                return mapResultSetToLabSeat(rs);
            } else {
                logger.warn("Lab seat not found in database: {}", seatID);
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat: {}", seatID, e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat: {}", seatID, e);
            return null;
        }
    }
    
   
    public List<LabSeat> getLabSeatsByLabId(String labID) {
        String query = "SELECT * FROM labSeats WHERE labID = ? ORDER BY seatID ASC";
        List<LabSeat> seats = new ArrayList<>();
        
        try {
            logger.debug("Fetching all lab seats for lab: {}", labID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, labID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LabSeat seat = mapResultSetToLabSeat(rs);
                seats.add(seat);
            }
            
            logger.info("Retrieved {} lab seats for lab: {}", seats.size(), labID);
            return seats;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seats for lab: {}", labID, e);
            return seats;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seats for lab: {}", labID, e);
            return seats;
        }
    }
    

    public List<LabSeat> getAllLabSeats() {
        String query = "SELECT * FROM labSeats";
        List<LabSeat> seats = new ArrayList<>();
        
        try {
            logger.debug("Fetching all lab seats from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                LabSeat seat = mapResultSetToLabSeat(rs);
                seats.add(seat);
            }
            
            logger.info("Retrieved {} lab seats from database", seats.size());
            return seats;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching all lab seats", e);
            return seats;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all lab seats", e);
            return seats;
        }
    }
    

    public boolean updateLabSeat(LabSeat labSeat, String seatCode) {
        String query = "UPDATE labSeats SET labID = ?, seatCode = ? WHERE seatID = ?";
        
        try {
            logger.debug("Updating lab seat in database: {}", labSeat.getSeatID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, labSeat.getSeatLocation().getLabID());
            pstmt.setString(2, seatCode);
            pstmt.setString(3, String.valueOf(labSeat.getSeatID()));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat updated successfully: {}", labSeat.getSeatID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while updating lab seat: {}", labSeat.getSeatID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab seat: {}", labSeat.getSeatID(), e);
            return false;
        }
    }
    
    /**
     * Deletes a lab seat from the database.
     * 
     * @param seatID the seat ID of the lab seat to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteLabSeat(String seatID) {
        String query = "DELETE FROM labSeats WHERE seatID = ?";
        
        try {
            logger.debug("Deleting lab seat from database: {}", seatID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, seatID);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat deleted successfully: {}", seatID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while deleting lab seat: {}", seatID, e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab seat: {}", seatID, e);
            return false;
        }
    }
    
    /**
     * Maps a ResultSet row to a LabSeat object.
     * 
     * @param rs the ResultSet containing lab seat data
     * @return a LabSeat object
     * @throws SQLException if database access error occurs
     */
    private LabSeat mapResultSetToLabSeat(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to LabSeat object");
        
        int seatID = rs.getInt("seatID");
        String labID = rs.getString("labID");
        
        // For a full implementation, you would retrieve the Lab details from database
        // For now, creating a basic Lab with only labID
        Lab lab = new Lab();
        lab.setLabID(labID);
        
        return new LabSeat(seatID, lab);
    }
}
