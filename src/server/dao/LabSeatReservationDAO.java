package server.dao;

import server.model.LabSeatReservation;
import server.model.LabSeat;
import server.model.Lab;
import server.model.Reservation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LabSeatReservation entities.
 * Handles all database operations (CRUD) related to lab seat reservations.
 * 
 * Database Schema (Composite Primary Key):
 * - studentUserID (PK, FK → Student.userID, INT)
 * - seatID (PK, FK → LabSeat.seatID, VARCHAR(20))
 * - reservationDateTime (PK, DATETIME/TIMESTAMP)
 * - durationInHours (INT, CHECK > 0)
 * - approvalStatus (VARCHAR(20))
 * - approvedByEmployeeID (FK → Employee.userID, INT, NULL)
 * - lastUpdated (DATETIME/TIMESTAMP)
 * 
 * Operations supported:
 * - Create: insertLabSeatReservation()
 * - Read Individual: getLabSeatReservationByCompositeKey()
 * - Read All: getReservationsByStudentId(), getReservationsByStatus(), getReservationsByApprovedBy(), getAllLabSeatReservations()
 * - Update: updateLabSeatReservation()
 * - Delete: deleteLabSeatReservation()
 */
public class LabSeatReservationDAO {
    private static final Logger logger = LogManager.getLogger(LabSeatReservationDAO.class);
    private Connection conn;

    public LabSeatReservationDAO(Connection conn) {
        this.conn = conn;
        logger.debug("LabSeatReservationDAO initialized with connection");
    }
    
    /**
     * Inserts a new lab seat reservation into the database.
     * 
     * @param studentUserID the student user ID
     * @param seatID the seat ID
     * @param reservationDateTime the reservation date and time
     * @param durationInHours the duration in hours
     * @param approvalStatus the approval status
     * @param approvedByEmployeeID the employee ID who approved (can be null)
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertLabSeatReservation(int studentUserID, String seatID, LocalDateTime reservationDateTime,
                                           int durationInHours, String approvalStatus, Integer approvedByEmployeeID) {
        String query = "INSERT INTO labSeatReservations (studentUserID, seatID, reservationDateTime, durationInHours, " +
                       "approvalStatus, approvedByEmployeeID, lastUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            logger.debug("Inserting lab seat reservation into database: student {}, seat {}, datetime {}", 
                        studentUserID, seatID, reservationDateTime);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, seatID);
            pstmt.setTimestamp(3, Timestamp.valueOf(reservationDateTime));
            pstmt.setInt(4, durationInHours);
            pstmt.setString(5, approvalStatus);
            if (approvedByEmployeeID != null) {
                pstmt.setInt(6, approvedByEmployeeID);
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat reservation inserted successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while inserting lab seat reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab seat reservation", e);
            return false;
        }
    }
    
    /**
     * Retrieves a lab seat reservation by composite key from the database.
     * 
     * @param studentUserID the student user ID
     * @param seatID the seat ID
     * @param reservationDateTime the reservation date and time
     * @return the LabSeatReservation object if found, null otherwise
     */
    public LabSeatReservation getLabSeatReservationByCompositeKey(int studentUserID, String seatID, LocalDateTime reservationDateTime) {
        String query = "SELECT * FROM labSeatReservations WHERE studentUserID = ? AND seatID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Fetching lab seat reservation by composite key");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, seatID);
            pstmt.setTimestamp(3, Timestamp.valueOf(reservationDateTime));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                logger.debug("Lab seat reservation found in database");
                return mapResultSetToLabSeatReservation(rs);
            } else {
                logger.warn("Lab seat reservation not found in database");
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservation", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservation", e);
            return null;
        }
    }
    
    /**
     * Retrieves all lab seat reservations for a specific student from the database.
     * 
     * @param studentUserID the student user ID
     * @return a list of LabSeatReservation objects, empty list if none found
     */
    public List<LabSeatReservation> getReservationsByStudentId(int studentUserID) {
        String query = "SELECT * FROM labSeatReservations WHERE studentUserID = ? ORDER BY reservationDateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching lab seat reservations for student: {}", studentUserID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LabSeatReservation reservation = mapResultSetToLabSeatReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} lab seat reservations for student: {}", reservations.size(), studentUserID);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations for student: {}", studentUserID, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations for student: {}", studentUserID, e);
            return reservations;
        }
    }
    
    /**
     * Retrieves all lab seat reservations with a specific approval status from the database.
     * 
     * @param status the approval status
     * @return a list of LabSeatReservation objects, empty list if none found
     */
    public List<LabSeatReservation> getReservationsByStatus(String status) {
        String query = "SELECT * FROM labSeatReservations WHERE approvalStatus = ? ORDER BY reservationDateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching lab seat reservations with status: {}", status);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LabSeatReservation reservation = mapResultSetToLabSeatReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} lab seat reservations with status: {}", reservations.size(), status);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations by status: {}", status, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations by status: {}", status, e);
            return reservations;
        }
    }
    
    /**
     * Retrieves all lab seat reservations approved by a specific employee from the database.
     * 
     * @param approvedByEmployeeID the employee ID
     * @return a list of LabSeatReservation objects, empty list if none found
     */
    public List<LabSeatReservation> getReservationsByApprovedBy(int approvedByEmployeeID) {
        String query = "SELECT * FROM labSeatReservations WHERE approvedByEmployeeID = ? ORDER BY reservationDateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching lab seat reservations approved by employee: {}", approvedByEmployeeID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, approvedByEmployeeID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LabSeatReservation reservation = mapResultSetToLabSeatReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} lab seat reservations approved by employee: {}", reservations.size(), approvedByEmployeeID);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations by approved employee: {}", approvedByEmployeeID, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations by approved employee: {}", approvedByEmployeeID, e);
            return reservations;
        }
    }
    
    /**
     * Retrieves all lab seat reservations from the database.
     * 
     * @return a list of all LabSeatReservation objects, empty list if none found
     */
    public List<LabSeatReservation> getAllLabSeatReservations() {
        String query = "SELECT * FROM labSeatReservations ORDER BY reservationDateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching all lab seat reservations from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                LabSeatReservation reservation = mapResultSetToLabSeatReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} lab seat reservations from database", reservations.size());
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching all lab seat reservations", e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all lab seat reservations", e);
            return reservations;
        }
    }
    
    /**
     * Updates an existing lab seat reservation in the database.
     * 
     * @param studentUserID the student user ID
     * @param seatID the seat ID
     * @param reservationDateTime the reservation date and time
     * @param durationInHours the updated duration in hours
     * @param approvalStatus the updated approval status
     * @param approvedByEmployeeID the updated employee ID who approved (can be null)
     * @return true if update was successful, false otherwise
     */
    public boolean updateLabSeatReservation(int studentUserID, String seatID, LocalDateTime reservationDateTime,
                                           int durationInHours, String approvalStatus, Integer approvedByEmployeeID) {
        String query = "UPDATE labSeatReservations SET durationInHours = ?, approvalStatus = ?, " +
                       "approvedByEmployeeID = ?, lastUpdated = ? WHERE studentUserID = ? AND seatID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Updating lab seat reservation in database");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, durationInHours);
            pstmt.setString(2, approvalStatus);
            if (approvedByEmployeeID != null) {
                pstmt.setInt(3, approvedByEmployeeID);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(5, studentUserID);
            pstmt.setString(6, seatID);
            pstmt.setTimestamp(7, Timestamp.valueOf(reservationDateTime));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat reservation updated successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while updating lab seat reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab seat reservation", e);
            return false;
        }
    }
    
    /**
     * Deletes a lab seat reservation from the database.
     * 
     * @param studentUserID the student user ID
     * @param seatID the seat ID
     * @param reservationDateTime the reservation date and time
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteLabSeatReservation(int studentUserID, String seatID, LocalDateTime reservationDateTime) {
        String query = "DELETE FROM labSeatReservations WHERE studentUserID = ? AND seatID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Deleting lab seat reservation from database");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, seatID);
            pstmt.setTimestamp(3, Timestamp.valueOf(reservationDateTime));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Lab seat reservation deleted successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while deleting lab seat reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab seat reservation", e);
            return false;
        }
    }
    
    /**
     * Maps a ResultSet row to a LabSeatReservation object.
     * 
     * @param rs the ResultSet containing lab seat reservation data
     * @return a LabSeatReservation object
     * @throws SQLException if database access error occurs
     */
    private LabSeatReservation mapResultSetToLabSeatReservation(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to LabSeatReservation object");
        
        // Note: This maps to the Reservation composite key structure
        // Map is adapted since LabSeatReservation extends Reservation
        int studentUserID = rs.getInt("studentUserID");
        String seatID = rs.getString("seatID");
        LocalDateTime reservationDateTime = rs.getTimestamp("reservationDateTime").toLocalDateTime();
        int durationInHours = rs.getInt("durationInHours");
        String approvalStatus = rs.getString("approvalStatus");
        Integer approvedByEmployeeID = rs.getObject("approvedByEmployeeID") != null ? rs.getInt("approvedByEmployeeID") : null;
        
        // Create a basic LabSeat with seatID
        LabSeat labSeat = new LabSeat();
        labSeat.setSeatID((int) Long.parseLong(seatID));
        
        // Use placeholder values for reservationID and studentID (from Student.userID)
        int reservationID = studentUserID; // Placeholder
        String studentID = String.valueOf(studentUserID); // Placeholder
        String approvedByStr = approvedByEmployeeID != null ? String.valueOf(approvedByEmployeeID) : "";
        
        return new LabSeatReservation(reservationID, studentID, reservationDateTime, durationInHours, 
                                     Reservation.ReservationStatus.valueOf(approvalStatus), approvedByStr, labSeat);
    }
}
