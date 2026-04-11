package server.dao;

import server.model.Equipment;
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


public class EquipmentDAO {
    private static final Logger logger = LogManager.getLogger(EquipmentDAO.class);
    private Connection conn;

    public EquipmentDAO(Connection conn) {
        this.conn = conn;
        logger.debug("EquipmentDAO initialized with connection");
    }
    

    public boolean insertEquipment(Equipment equipment, String labID) {
        String query = "INSERT INTO equipment (equipmentID, labID, description, status) " +
                       "VALUES (?, ?, ?, ?)";
        
        try {
            logger.debug("Inserting equipment into database: {}", equipment.getEquipmentID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, equipment.getEquipmentID());
            pstmt.setString(2, labID);
            pstmt.setString(3, equipment.getDescription());
            pstmt.setString(4, equipment.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Equipment inserted successfully: {}", equipment.getEquipmentID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while inserting equipment: {}", equipment.getEquipmentID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting equipment: {}", equipment.getEquipmentID(), e);
            return false;
        }
    }
    

    public Equipment getEquipmentById(String equipmentID) {
        String query = "SELECT * FROM equipment WHERE equipmentID = ?";
        
        try {
            logger.debug("Fetching equipment from database with ID: {}", equipmentID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, equipmentID);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                logger.debug("Equipment found in database: {}", equipmentID);
                return mapResultSetToEquipment(rs);
            } else {
                logger.warn("Equipment not found in database: {}", equipmentID);
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment: {}", equipmentID, e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment: {}", equipmentID, e);
            return null;
        }
    }
    
    
    public List<Equipment> getEquipmentByStatus(String status) {
        String query = "SELECT * FROM equipment WHERE status = ?";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try {
            logger.debug("Fetching equipment with status: {}", status);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Equipment equipment = mapResultSetToEquipment(rs);
                equipmentList.add(equipment);
            }
            
            logger.info("Retrieved {} equipment with status: {}", equipmentList.size(), status);
            return equipmentList;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching equipment by status: {}", status, e);
            return equipmentList;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching equipment by status: {}", status, e);
            return equipmentList;
        }
    }
    

    public List<Equipment> getAllEquipment() {
        String query = "SELECT * FROM equipment";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try {
            logger.debug("Fetching all equipment from database");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Equipment equipment = mapResultSetToEquipment(rs);
                equipmentList.add(equipment);
            }
            
            logger.info("Retrieved {} equipment from database", equipmentList.size());
            return equipmentList;
            
        } catch (SQLException e) {
            logger.error("SQLException while fetching all equipment", e);
            return equipmentList;
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all equipment", e);
            return equipmentList;
        }
    }
    

    public boolean updateEquipment(Equipment equipment, String labID) {
        String query = "UPDATE equipment SET labID = ?, description = ?, status = ? WHERE equipmentID = ?";
        
        try {
            logger.debug("Updating equipment in database: {}", equipment.getEquipmentID());
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, labID);
            pstmt.setString(2, equipment.getDescription());
            pstmt.setString(3, equipment.getStatus());
            pstmt.setString(4, equipment.getEquipmentID());
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Equipment updated successfully: {}", equipment.getEquipmentID());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while updating equipment: {}", equipment.getEquipmentID(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while updating equipment: {}", equipment.getEquipmentID(), e);
            return false;
        }
    }
    

    public boolean deleteEquipment(String equipmentID) {
        String query = "DELETE FROM equipment WHERE equipmentID = ?";
        
        try {
            logger.debug("Deleting equipment from database: {}", equipmentID);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, equipmentID);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Equipment deleted successfully: {}", equipmentID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("SQLException while deleting equipment: {}", equipmentID, e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting equipment: {}", equipmentID, e);
            return false;
        }
    }
    

    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        logger.debug("Mapping ResultSet to Equipment object");
        
        String equipmentID = rs.getString("equipmentID");
        String description = rs.getString("description");
        String status = rs.getString("status");
        

        Lab lab = new Lab();
        return new Equipment(equipmentID, description, lab, status);
    }
}
