package server.dao;

import server.model.EquipmentReservation;
import server.model.Equipment;
import server.model.Location;
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


public class EquipmentReservationDAO {
    private static final Logger logger = LogManager.getLogger(EquipmentReservationDAO.class);
    private Connection conn;

    public EquipmentReservationDAO(Connection conn) {
        this.conn = conn;
        logger.debug("EquipmentReservationDAO initialized with connection");
    }
    
   
    public boolean insertEquipmentReservation(int studentUserID, String equipmentID, LocalDateTime reservationDateTime,
                                             int durationInHours, String approvalStatus, Integer approvedByEmployeeID) {
        String query = "INSERT INTO equipmentReservations (studentUserID, equipmentID, reservationDateTime, durationInHours, " +
                       "approvalStatus, approvedByEmployeeID, lastUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            logger.debug("Inserting equipment reservation into database: student {}, equipment {}, datetime {}", 
                        studentUserID, equipmentID, reservationDateTime);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, equipmentID);
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
            logger.info("Equipment reservation inserted successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while inserting equipment reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting equipment reservation", e);
            return false;
        }
    }
    

    public EquipmentReservation getEquipmentReservationByCompositeKey(int studentUserID, String equipmentID, LocalDateTime reservationDateTime) {
        String query = "SELECT * FROM equipmentReservations WHERE studentUserID = ? AND equipmentID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Fetching equipment reservation by composite key");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, equipmentID);
            pstmt.setTimestamp(3, Timestamp.valueOf(reservationDateTime));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                logger.debug("Equipment reservation found in database");
                return mapResultSetToEquipmentReservation(rs);
            } else {
                logger.warn("Equipment reservation not found in database");
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservation", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservation", e);
            return null;
        }
    }
    
    /**
     * Retrieves all equipment reservations for a specific student from the database.
     * 
     * @param studentUserID the student user ID
     * @return a list of EquipmentReservation objects, empty list if none found
     */
    public List<EquipmentReservation> getReservationsByStudentId(int studentUserID) {
        String query = "SELECT * FROM equipmentReservations WHERE studentUserID = ? ORDER BY reservationDateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching equipment reservations for student: {}", studentUserID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EquipmentReservation reservation = mapResultSetToEquipmentReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} equipment reservations for student: {}", reservations.size(), studentUserID);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations for student: {}", studentUserID, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations for student: {}", studentUserID, e);
            return reservations;
        }
    }
    
    /**
     * Retrieves all equipment reservations with a specific approval status from the database.
     * 
     * @param status the approval status
     * @return a list of EquipmentReservation objects, empty list if none found
     */
    public List<EquipmentReservation> getReservationsByStatus(String status) {
        String query = "SELECT * FROM equipmentReservations WHERE approvalStatus = ? ORDER BY reservationDateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching equipment reservations with status: {}", status);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EquipmentReservation reservation = mapResultSetToEquipmentReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} equipment reservations with status: {}", reservations.size(), status);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations by status: {}", status, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations by status: {}", status, e);
            return reservations;
        }
    }
    
 
    public List<EquipmentReservation> getReservationsByApprovedBy(int approvedByEmployeeID) {
        String query = "SELECT * FROM equipmentReservations WHERE approvedByEmployeeID = ? ORDER BY reservationDateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching equipment reservations approved by employee: {}", approvedByEmployeeID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, approvedByEmployeeID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EquipmentReservation reservation = mapResultSetToEquipmentReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} equipment reservations approved by employee: {}", reservations.size(), approvedByEmployeeID);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations by approved employee: {}", approvedByEmployeeID, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations by approved employee: {}", approvedByEmployeeID, e);
            return reservations;
        }
    }
    
  
    public List<EquipmentReservation> getReservationsByEquipmentId(String equipmentID) {
        String query = "SELECT * FROM equipmentReservations WHERE equipmentID = ? ORDER BY reservationDateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching equipment reservations for equipment: {}", equipmentID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, equipmentID);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                EquipmentReservation reservation = mapResultSetToEquipmentReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} equipment reservations for equipment: {}", reservations.size(), equipmentID);
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations for equipment: {}", equipmentID, e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations for equipment: {}", equipmentID, e);
            return reservations;
        }
    }
    

    public List<EquipmentReservation> getAllEquipmentReservations() {
        String query = "SELECT * FROM equipmentReservations ORDER BY reservationDateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();
        
        try {
            logger.debug("Fetching all equipment reservations from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                EquipmentReservation reservation = mapResultSetToEquipmentReservation(rs);
                reservations.add(reservation);
            }
            
            logger.info("Retrieved {} equipment reservations from database", reservations.size());
            return reservations;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching all equipment reservations", e);
            return reservations;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all equipment reservations", e);
            return reservations;
        }
    }
    
  
    public boolean updateEquipmentReservation(int studentUserID, String equipmentID, LocalDateTime reservationDateTime,
                                             int durationInHours, String approvalStatus, Integer approvedByEmployeeID) {
        String query = "UPDATE equipmentReservations SET durationInHours = ?, approvalStatus = ?, " +
                       "approvedByEmployeeID = ?, lastUpdated = ? WHERE studentUserID = ? AND equipmentID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Updating equipment reservation in database");
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
            pstmt.setString(6, equipmentID);
            pstmt.setTimestamp(7, Timestamp.valueOf(reservationDateTime));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Equipment reservation updated successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while updating equipment reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while updating equipment reservation", e);
            return false;
        }
    }
    
    /**
     * Deletes an equipment reservation from the database.
     * 
     * @param studentUserID the student user ID
     * @param equipmentID the equipment ID
     * @param reservationDateTime the reservation date and time
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteEquipmentReservation(int studentUserID, String equipmentID, LocalDateTime reservationDateTime) {
        String query = "DELETE FROM equipmentReservations WHERE studentUserID = ? AND equipmentID = ? AND reservationDateTime = ?";
        
        try {
            logger.debug("Deleting equipment reservation from database");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, studentUserID);
            pstmt.setString(2, equipmentID);
            pstmt.setTimestamp(3, Timestamp.valueOf(reservationDateTime));
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Equipment reservation deleted successfully");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while deleting equipment reservation", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting equipment reservation", e);
            return false;
        }
    }
    

    private EquipmentReservation mapResultSetToEquipmentReservation(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to EquipmentReservation object");
        
        int studentUserID = rs.getInt("studentUserID");
        String equipmentID = rs.getString("equipmentID");
        LocalDateTime reservationDateTime = rs.getTimestamp("reservationDateTime").toLocalDateTime();
        int durationInHours = rs.getInt("durationInHours");
        String approvalStatus = rs.getString("approvalStatus");
        Integer approvedByEmployeeID = rs.getObject("approvedByEmployeeID") != null ? rs.getInt("approvedByEmployeeID") : null;
        
        // Create a basic Equipment with equipmentID
        Equipment equipment = new Equipment();
        equipment.setEquipmentID(equipmentID);
        
        // Use placeholder values for reservationID and studentID (from Student.userID)
        int reservationID = studentUserID; // Placeholder
        String studentID = String.valueOf(studentUserID); // Placeholder
        String approvedByStr = approvedByEmployeeID != null ? String.valueOf(approvedByEmployeeID) : "";
        
        return new EquipmentReservation(reservationID, studentID, reservationDateTime, durationInHours, 
                                       server.model.Reservation.ReservationStatus.valueOf(approvalStatus), approvedByStr, equipment);
    }
}
