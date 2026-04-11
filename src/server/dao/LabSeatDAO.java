package server.dao;

import server.model.Lab;
import server.model.LabSeat;

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
    private final Connection conn;

    public LabSeatDAO(Connection conn) {
        this.conn = conn;
    }

    // INITIALIZES LAB SEAT TABLE IN DATABASE
    public boolean initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `LabSeat` ("
                + "seatID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                + "seatCode VARCHAR(20) NOT NULL, "
                + "labID VARCHAR(20) NOT NULL, "
                + "FOREIGN KEY (labID) REFERENCES `Lab`(labID)"
                + ");";

        try (PreparedStatement dbStmt = conn.prepareStatement(sql)) {
            dbStmt.execute();
            return true;
        } catch (SQLException sqle) {
            logger.error("Failed to initialize LabSeat table", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while initializing LabSeat table", e);
        }
        return false;
    }

    // CREATE LAB SEAT RECORD IN DB
    public boolean saveLabSeat(LabSeat labSeat) {
        String sql = "INSERT INTO `LabSeat` "
                + "(seatCode, labID) "
                + "VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, labSeat.getSeatCode());
            pstmt.setString(2, labSeat.getSeatLocation().getLabID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedID = rs.getInt(1);
                        labSeat.setSeatID(generatedID);
                    }
                }
                logger.info("Lab seat saved successfully: {}", labSeat.getSeatCode());
                return true;
            }

        } catch (SQLException e) {
            logger.error("SQLException while inserting lab seat", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab seat", e);
        }
        return false;
    }

    public LabSeat getLabSeatById(int seatID) {
        String query = "SELECT * FROM `LabSeat` WHERE seatID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, seatID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Retrieved lab seat by ID: {}", seatID);
                    return mapResultSetToLabSeat(rs);
                }
            }

            logger.warn("Lab seat not found for ID: {}", seatID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seat", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seat", e);
        }
        return null;
    }

    public List<LabSeat> getLabSeatsByLabId(String labID) {
        String query = "SELECT * FROM `LabSeat` WHERE labID = ? ORDER BY seatID";
        List<LabSeat> seats = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, labID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToLabSeat(rs));
                }
            }

            logger.info("Retrieved {} lab seats for lab: {}", seats.size(), labID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seats by labID: {}", labID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seats by labID: {}", labID, e);
        }

        return seats;
    }

    public List<LabSeat> getLabSeatsBySeatCode(String seatCode) {
        String query = "SELECT * FROM `LabSeat` WHERE seatCode = ? ORDER BY seatID";
        List<LabSeat> seats = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, seatCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToLabSeat(rs));
                }
            }

            logger.info("Retrieved {} lab seats with seatCode: {}", seats.size(), seatCode);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab seats by seatCode: {}", seatCode, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab seats by seatCode: {}", seatCode, e);
        }

        return seats;
    }

    public List<LabSeat> getAllLabSeats() {
        String query = "SELECT * FROM `LabSeat` ORDER BY seatID";
        List<LabSeat> seats = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                seats.add(mapResultSetToLabSeat(rs));
            }

            logger.info("Retrieved {} lab seats from database", seats.size());
        } catch (SQLException e) {
            logger.error("SQLException while fetching all lab seats", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all lab seats", e);
        }

        return seats;
    }

    public boolean updateLabSeat(LabSeat updatedLabSeat) {
        String query = "UPDATE `LabSeat` SET "
                + "seatCode = ?, "
                + "labID = ? "
                + "WHERE seatID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedLabSeat.getSeatCode());
            pstmt.setString(2, updatedLabSeat.getSeatLocation().getLabID());
            pstmt.setInt(3, updatedLabSeat.getSeatID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab seat updated successfully: {}", updatedLabSeat.getSeatID());
            } else {
                logger.warn("No lab seat updated for ID: {}", updatedLabSeat.getSeatID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while updating lab seat", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab seat", e);
        }
        return false;
    }

    public boolean deleteLabSeat(int seatID) {
        String query = "DELETE FROM `LabSeat` WHERE seatID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, seatID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab seat deleted successfully: {}", seatID);
            } else {
                logger.warn("No lab seat deleted for ID: {}", seatID);
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while deleting lab seat", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab seat", e);
        }
        return false;
    }

    private LabSeat mapResultSetToLabSeat(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to LabSeat object");

        int seatID = rs.getInt("seatID");
        String seatCode = rs.getString("seatCode");
        String labID = rs.getString("labID");

        Lab lab = new Lab();
        lab.setLabID(labID);

        LabSeat labSeat = new LabSeat();
        labSeat.setSeatID(seatID);
        labSeat.setSeatCode(seatCode);
        labSeat.setSeatLocation(lab);

        return labSeat;
    }
}