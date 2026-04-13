package dao;

import model.Equipment;
import model.EquipmentReservation;
import model.Reservation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EquipmentReservationDAO {
    private static final Logger logger = LogManager.getLogger(EquipmentReservationDAO.class);
    private final Connection conn;
    private final EquipmentDAO equipmentDAO;

    public EquipmentReservationDAO(Connection conn) {
        this.conn = conn;
        this.equipmentDAO = new EquipmentDAO(conn);
    }

    // INITIALIZES EQUIPMENT RESERVATION TABLE IN DATABASE
    public boolean initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `EquipmentReservation` ("
                + "reservationID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                + "studentID VARCHAR(20) NOT NULL, "
                + "dateTime DATETIME NOT NULL, "
                + "durationInHours INT NOT NULL, "
                + "approvalStatus VARCHAR(20) NOT NULL, "
                + "approvedBy VARCHAR(50), "
                + "lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "equipmentID VARCHAR(20) NOT NULL"
                + ");";

        try (PreparedStatement dbStmt = conn.prepareStatement(sql)) {
            dbStmt.execute();
            return true;
        } catch (SQLException sqle) {
            logger.error("Failed to initialize EquipmentReservation table", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while initializing EquipmentReservation table", e);
        }
        return false;
    }

    // CREATE EQUIPMENT RESERVATION RECORD IN DB
    public boolean saveEquipmentReservation(EquipmentReservation equipRes) {
        String sql = "INSERT INTO `EquipmentReservation` "
                + "(studentID, dateTime, durationInHours, approvalStatus, approvedBy, lastUpdated, equipmentID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, equipRes.getStudentID());
            pstmt.setTimestamp(2, Timestamp.valueOf(equipRes.getDateTime()));
            pstmt.setInt(3, equipRes.getDurationInHours());
            pstmt.setString(4, equipRes.getApprovalStatus().name());
            pstmt.setString(5, equipRes.getApprovedBy());

            if (equipRes.getLastUpdated() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(equipRes.getLastUpdated()));
            } else {
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }

            pstmt.setString(7, equipRes.getReservedItem().getEquipmentID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedID = rs.getInt(1);
                        equipRes.setReservationID(generatedID);
                    }
                }
                logger.info("Equipment reservation saved successfully for student: {}", equipRes.getStudentID());
                return true;
            }

        } catch (SQLException e) {
            logger.error("SQLException while inserting equipment reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting equipment reservation", e);
        }
        return false;
    }

    public EquipmentReservation getEquipmentReservationById(int reservationID) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reservationID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Retrieved equipment reservation by ID: {}", reservationID);
                    return mapResultSetToEquipmentReservation(rs);
                }
            }

            logger.warn("Equipment reservation not found for ID: {}", reservationID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservation", e);
        }
        return null;
    }

    public List<EquipmentReservation> getReservationsByStudentId(String studentID) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE studentID = ? ORDER BY dateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToEquipmentReservation(rs));
                }
            }

            logger.info("Retrieved {} equipment reservations for student: {}", reservations.size(), studentID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations for student: {}", studentID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations for student: {}", studentID, e);
        }

        return reservations;
    }

    public List<EquipmentReservation> getReservationsByStatus(String status) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE approvalStatus = ? ORDER BY dateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToEquipmentReservation(rs));
                }
            }

            logger.info("Retrieved {} equipment reservations with status: {}", reservations.size(), status);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations by status: {}", status, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations by status: {}", status, e);
        }

        return reservations;
    }

    public List<EquipmentReservation> getReservationsByApprovedBy(String approvedBy) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE approvedBy = ? ORDER BY dateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, approvedBy);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToEquipmentReservation(rs));
                }
            }

            logger.info("Retrieved {} equipment reservations approved by: {}", reservations.size(), approvedBy);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations by approver: {}", approvedBy, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations by approver: {}", approvedBy, e);
        }

        return reservations;
    }

    public List<EquipmentReservation> getReservationsByEquipmentId(String equipmentID) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE equipmentID = ? ORDER BY dateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, equipmentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToEquipmentReservation(rs));
                }
            }

            logger.info("Retrieved {} equipment reservations for equipment: {}", reservations.size(), equipmentID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations for equipment: {}", equipmentID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations for equipment: {}", equipmentID, e);
        }

        return reservations;
    }

    public List<EquipmentReservation> getReservationsByEquipmentIdAndDate(String equipmentID, LocalDateTime date) {
        String query = "SELECT * FROM `EquipmentReservation` WHERE equipmentID = ? AND DATE(dateTime) = DATE(?) AND HOUR(dateTime) < 21 ORDER BY dateTime ASC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, equipmentID);
            pstmt.setTimestamp(2, Timestamp.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToEquipmentReservation(rs));
                }
            }

            logger.info("Retrieved {} equipment reservations for equipment: {} on date: {}", reservations.size(), equipmentID, date.toLocalDate());
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment reservations for equipment: {} on date: {}", equipmentID, date.toLocalDate(), e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment reservations for equipment: {} on date: {}", equipmentID, date.toLocalDate(), e);
        }

        return reservations;
    }

    public List<EquipmentReservation> getAllEquipmentReservations() {
        String query = "SELECT * FROM `EquipmentReservation` ORDER BY dateTime DESC";
        List<EquipmentReservation> reservations = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(mapResultSetToEquipmentReservation(rs));
            }

            logger.info("Retrieved {} equipment reservations from database", reservations.size());
        } catch (SQLException e) {
            logger.error("SQLException while fetching all equipment reservations", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all equipment reservations", e);
        }

        return reservations;
    }

    public boolean updateEquipmentReservation(EquipmentReservation updatedReservation) {
        String query = "UPDATE `EquipmentReservation` SET "
                + "studentID = ?, "
                + "dateTime = ?, "
                + "durationInHours = ?, "
                + "approvalStatus = ?, "
                + "approvedBy = ?, "
                + "lastUpdated = ?, "
                + "equipmentID = ? "
                + "WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedReservation.getStudentID());
            pstmt.setTimestamp(2, Timestamp.valueOf(updatedReservation.getDateTime()));
            pstmt.setInt(3, updatedReservation.getDurationInHours());
            pstmt.setString(4, updatedReservation.getApprovalStatus().name());
            pstmt.setString(5, updatedReservation.getApprovedBy());
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(7, updatedReservation.getReservedItem().getEquipmentID());
            pstmt.setInt(8, updatedReservation.getReservationID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Equipment reservation updated successfully for ID: {}", updatedReservation.getReservationID());
            } else {
                logger.warn("No equipment reservation updated for ID: {}", updatedReservation.getReservationID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while updating equipment reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while updating equipment reservation", e);
        }
        return false;
    }

    public boolean deleteEquipmentReservation(int reservationID) {
        String query = "DELETE FROM `EquipmentReservation` WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reservationID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Equipment reservation deleted successfully for ID: {}", reservationID);
            } else {
                logger.warn("No equipment reservation deleted for ID: {}", reservationID);
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while deleting equipment reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting equipment reservation", e);
        }
        return false;
    }

    private EquipmentReservation mapResultSetToEquipmentReservation(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to EquipmentReservation object");

        int reservationID = rs.getInt("reservationID");
        String studentID = rs.getString("studentID");
        LocalDateTime dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
        int durationInHours = rs.getInt("durationInHours");
        String approvalStatus = rs.getString("approvalStatus");
        String approvedBy = rs.getString("approvedBy");
        String equipmentID = rs.getString("equipmentID");

        // Load the full equipment record so downstream DTOs include description and location.
        Equipment equipment = equipmentDAO.getEquipmentById(equipmentID);
        if (equipment == null) {
            equipment = new Equipment();
            equipment.setEquipmentID(equipmentID);
        }

        EquipmentReservation reservation = new EquipmentReservation(
                reservationID,
                studentID,
                dateTime,
                durationInHours,
                Reservation.ReservationStatus.valueOf(approvalStatus),
                approvedBy,
                equipment
        );

        Timestamp lastUpdatedTs = rs.getTimestamp("lastUpdated");
        if (lastUpdatedTs != null) {
            reservation.setLastUpdated(lastUpdatedTs.toLocalDateTime());
        }

        return reservation;
    }
}
