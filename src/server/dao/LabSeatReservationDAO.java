package dao;

import model.LabSeat;
import model.LabSeatReservation;
import model.Reservation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LabSeatReservationDAO {
    private static final Logger logger = LogManager.getLogger(LabSeatReservationDAO.class);
    private final Connection conn;
    private final LabSeatDAO labSeatDAO;

    public LabSeatReservationDAO(Connection conn) {
        this.conn = conn;
        this.labSeatDAO = new LabSeatDAO(conn);
    }

    // INITIALIZES LAB SEAT RESERVATION TABLE IN DATABASE
    public boolean initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `LabSeatReservation` ("
                + "reservationID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                + "studentID VARCHAR(20) NOT NULL, "
                + "dateTime DATETIME NOT NULL, "
                + "durationInHours INT NOT NULL, "
                + "approvalStatus VARCHAR(20) NOT NULL, "
                + "approvedBy VARCHAR(50), "
                + "lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "seatID INT NOT NULL, "
                + "FOREIGN KEY (seatID) REFERENCES `LabSeat`(seatID)"
                + ");";

        try (PreparedStatement dbStmt = conn.prepareStatement(sql)) {
            dbStmt.execute();
            return true;
        } catch (SQLException sqle) {
            logger.error("Failed to initialize LabSeatReservation table", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while initializing LabSeatReservation table", e);
        }
        return false;
    }

    // CREATE LAB SEAT RESERVATION RECORD IN DB
    public boolean saveLabSeatReservation(LabSeatReservation seatRes) {
        String sql = "INSERT INTO `LabSeatReservation` "
                + "(studentID, dateTime, durationInHours, approvalStatus, approvedBy, lastUpdated, seatID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, seatRes.getStudentID());
            pstmt.setTimestamp(2, Timestamp.valueOf(seatRes.getDateTime()));
            pstmt.setInt(3, seatRes.getDurationInHours());
            pstmt.setString(4, seatRes.getApprovalStatus().name());
            pstmt.setString(5, seatRes.getApprovedBy());

            if (seatRes.getLastUpdated() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(seatRes.getLastUpdated()));
            } else {
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }

            pstmt.setInt(7, seatRes.getReservedSeat().getSeatID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedID = rs.getInt(1);
                        seatRes.setReservationID(generatedID);
                    }
                }
                logger.info("Lab seat reservation saved successfully for student: {}", seatRes.getStudentID());
                return true;
            }

        } catch (SQLException e) {
            logger.error("SQLException while inserting lab seat reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab seat reservation", e);
        }
        return false;
    }

    public LabSeatReservation getLabSeatReservationById(int reservationID) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reservationID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Retrieved lab seat reservation by ID: {}", reservationID);
                    return mapResultSetToLabSeatReservation(rs);
                }
            }

            logger.warn("Lab seat reservation not found for ID: {}", reservationID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservation", e);
        }
        return null;
    }

    public List<LabSeatReservation> getReservationsByStudentId(String studentID) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE studentID = ? ORDER BY dateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToLabSeatReservation(rs));
                }
            }

            logger.info("Retrieved {} lab seat reservations for student: {}", reservations.size(), studentID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations for student: {}", studentID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations for student: {}", studentID, e);
        }

        return reservations;
    }

    public List<LabSeatReservation> getReservationsByStatus(String status) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE approvalStatus = ? ORDER BY dateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToLabSeatReservation(rs));
                }
            }

            logger.info("Retrieved {} lab seat reservations with status: {}", reservations.size(), status);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations by status: {}", status, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations by status: {}", status, e);
        }

        return reservations;
    }

    public List<LabSeatReservation> getReservationsByApprovedBy(String approvedBy) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE approvedBy = ? ORDER BY dateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, approvedBy);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToLabSeatReservation(rs));
                }
            }

            logger.info("Retrieved {} lab seat reservations approved by: {}", reservations.size(), approvedBy);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations by approver: {}", approvedBy, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations by approver: {}", approvedBy, e);
        }

        return reservations;
    }

    public List<LabSeatReservation> getReservationsBySeatId(int seatID) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE seatID = ? ORDER BY dateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, seatID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToLabSeatReservation(rs));
                }
            }

            logger.info("Retrieved {} lab seat reservations for seat: {}", reservations.size(), seatID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations for seat: {}", seatID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations for seat: {}", seatID, e);
        }

        return reservations;
    }

    public List<LabSeatReservation> getReservationsBySeatIdAndDate(int seatID, LocalDateTime date) {
        String query = "SELECT * FROM `LabSeatReservation` WHERE seatID = ? AND DATE(dateTime) = DATE(?) AND HOUR(dateTime) < 21 ORDER BY dateTime ASC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, seatID);
            pstmt.setTimestamp(2, Timestamp.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToLabSeatReservation(rs));
                }
            }

            logger.info("Retrieved {} lab seat reservations for seat: {} on date: {}", reservations.size(), seatID, date.toLocalDate());
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat reservations for seat: {} on date: {}", seatID, date.toLocalDate(), e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat reservations for seat: {} on date: {}", seatID, date.toLocalDate(), e);
        }

        return reservations;
    }

    public List<LabSeatReservation> getAllLabSeatReservations() {
        String query = "SELECT * FROM `LabSeatReservation` ORDER BY dateTime DESC";
        List<LabSeatReservation> reservations = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(mapResultSetToLabSeatReservation(rs));
            }

            logger.info("Retrieved {} lab seat reservations from database", reservations.size());
        } catch (SQLException e) {
            logger.error("SQLException while fetching all lab seat reservations", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all lab seat reservations", e);
        }

        return reservations;
    }

    public boolean updateLabSeatReservation(LabSeatReservation updatedReservation) {
        String query = "UPDATE `LabSeatReservation` SET "
                + "studentID = ?, "
                + "dateTime = ?, "
                + "durationInHours = ?, "
                + "approvalStatus = ?, "
                + "approvedBy = ?, "
                + "lastUpdated = ?, "
                + "seatID = ? "
                + "WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedReservation.getStudentID());
            pstmt.setTimestamp(2, Timestamp.valueOf(updatedReservation.getDateTime()));
            pstmt.setInt(3, updatedReservation.getDurationInHours());
            pstmt.setString(4, updatedReservation.getApprovalStatus().name());
            pstmt.setString(5, updatedReservation.getApprovedBy());
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, updatedReservation.getReservedSeat().getSeatID());
            pstmt.setInt(8, updatedReservation.getReservationID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab seat reservation updated successfully for ID: {}", updatedReservation.getReservationID());
            } else {
                logger.warn("No lab seat reservation updated for ID: {}", updatedReservation.getReservationID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while updating lab seat reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab seat reservation", e);
        }
        return false;
    }

    public boolean deleteLabSeatReservation(int reservationID) {
        String query = "DELETE FROM `LabSeatReservation` WHERE reservationID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reservationID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab seat reservation deleted successfully for ID: {}", reservationID);
            } else {
                logger.warn("No lab seat reservation deleted for ID: {}", reservationID);
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while deleting lab seat reservation", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab seat reservation", e);
        }
        return false;
    }

    private LabSeatReservation mapResultSetToLabSeatReservation(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to LabSeatReservation object");

        int reservationID = rs.getInt("reservationID");
        String studentID = rs.getString("studentID");
        LocalDateTime dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
        int durationInHours = rs.getInt("durationInHours");
        String approvalStatus = rs.getString("approvalStatus");
        String approvedBy = rs.getString("approvedBy");
        int seatID = rs.getInt("seatID");

        // Load the full seat so seatCode and lab details are available to the client.
        LabSeat seat = labSeatDAO.getLabSeatById(seatID);
        if (seat == null) {
            seat = new LabSeat();
            seat.setSeatID(seatID);
        }

        LabSeatReservation reservation = new LabSeatReservation(
                reservationID,
                studentID,
                dateTime,
                durationInHours,
                Reservation.ReservationStatus.valueOf(approvalStatus),
                approvedBy,
                seat
        );

        Timestamp lastUpdatedTs = rs.getTimestamp("lastUpdated");
        if (lastUpdatedTs != null) {
            reservation.setLastUpdated(lastUpdatedTs.toLocalDateTime());
        }

        return reservation;
    }
}
