package server.dao;

import server.model.Equipment;
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

public class EquipmentDAO {
    private static final Logger logger = LogManager.getLogger(EquipmentDAO.class);
    private final Connection conn;

    public EquipmentDAO(Connection conn) {
        this.conn = conn;
    }

    // INITIALIZES EQUIPMENT TABLE IN DATABASE
    public boolean initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `Equipment` ("
                + "equipmentID VARCHAR(20) PRIMARY KEY NOT NULL, "
                + "description VARCHAR(255) NOT NULL, "
                + "labID VARCHAR(20) NOT NULL, "
                + "status VARCHAR(20) NOT NULL, "
                + "FOREIGN KEY (labID) REFERENCES `Lab`(labID)"
                + ");";

        try (PreparedStatement dbStmt = conn.prepareStatement(sql)) {
            dbStmt.execute();
            return true;
        } catch (SQLException sqle) {
            logger.error("Failed to initialize Equipment table", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while initializing Equipment table", e);
        }
        return false;
    }

    // CREATE EQUIPMENT RECORD IN DB
    public boolean saveEquipment(Equipment equipment) {
        String sql = "INSERT INTO `Equipment` "
                + "(equipmentID, description, labID, status) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipment.getEquipmentID());
            pstmt.setString(2, equipment.getDescription());
            pstmt.setString(3, equipment.getLocation().getLabID());
            pstmt.setString(4, equipment.getStatus().name());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Equipment saved successfully: {}", equipment.getEquipmentID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while inserting equipment", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting equipment", e);
        }
        return false;
    }

    public Equipment getEquipmentById(String equipmentID) {
        String query = "SELECT * FROM `Equipment` WHERE equipmentID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, equipmentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Retrieved equipment by ID: {}", equipmentID);
                    return mapResultSetToEquipment(rs);
                }
            }

            logger.warn("Equipment not found for ID: {}", equipmentID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment", e);
        }
        return null;
    }

    public List<Equipment> getEquipmentByLabId(String labID) {
        String query = "SELECT * FROM `Equipment` WHERE labID = ? ORDER BY equipmentID";
        List<Equipment> equipmentList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, labID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    equipmentList.add(mapResultSetToEquipment(rs));
                }
            }

            logger.info("Retrieved {} equipment items for lab: {}", equipmentList.size(), labID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment by labID: {}", labID, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment by labID: {}", labID, e);
        }

        return equipmentList;
    }

    public List<Equipment> getEquipmentByStatus(String status) {
        String query = "SELECT * FROM `Equipment` WHERE status = ? ORDER BY equipmentID";
        List<Equipment> equipmentList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    equipmentList.add(mapResultSetToEquipment(rs));
                }
            }

            logger.info("Retrieved {} equipment items with status: {}", equipmentList.size(), status);
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment by status: {}", status, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment by status: {}", status, e);
        }

        return equipmentList;
    }

    public List<Equipment> getAllEquipment() {
        String query = "SELECT * FROM `Equipment` ORDER BY equipmentID";
        List<Equipment> equipmentList = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }

            logger.info("Retrieved {} equipment items from database", equipmentList.size());
        } catch (SQLException e) {
            logger.error("SQLException while fetching all equipment", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all equipment", e);
        }

        return equipmentList;
    }

    public boolean updateEquipment(Equipment updatedEquipment) {
        String query = "UPDATE `Equipment` SET "
                + "description = ?, "
                + "labID = ?, "
                + "status = ? "
                + "WHERE equipmentID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedEquipment.getDescription());
            pstmt.setString(2, updatedEquipment.getLocation().getLabID());
            pstmt.setString(3, updatedEquipment.getStatus().name());
            pstmt.setString(4, updatedEquipment.getEquipmentID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Equipment updated successfully: {}", updatedEquipment.getEquipmentID());
            } else {
                logger.warn("No equipment updated for ID: {}", updatedEquipment.getEquipmentID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while updating equipment", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while updating equipment", e);
        }
        return false;
    }

    public boolean deleteEquipment(String equipmentID) {
        String query = "DELETE FROM `Equipment` WHERE equipmentID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, equipmentID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Equipment deleted successfully: {}", equipmentID);
            } else {
                logger.warn("No equipment deleted for ID: {}", equipmentID);
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while deleting equipment", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting equipment", e);
        }
        return false;
    }

    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to Equipment object");

        String equipmentID = rs.getString("equipmentID");
        String description = rs.getString("description");
        String labID = rs.getString("labID");
        String status = rs.getString("status");

        Lab lab = new Lab();
        lab.setLabID(labID);

        Equipment equipment = new Equipment();
        equipment.setEquipmentID(equipmentID);
        equipment.setDescription(description);
        equipment.setLocation(lab);
        equipment.setStatus(Equipment.EquipStatus.valueOf(status));

        return equipment;
    }
}