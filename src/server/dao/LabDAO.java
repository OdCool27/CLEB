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
    private final Connection conn;

    public LabDAO(Connection conn) {
        this.conn = conn;
    }

    // INITIALIZES LAB TABLE IN DATABASE
    public boolean initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `Lab` ("
                + "labID VARCHAR(20) PRIMARY KEY NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "roomName VARCHAR(50) NOT NULL, "
                + "building VARCHAR(100) NOT NULL, "
                + "floor INT NOT NULL, "
                + "campus VARCHAR(100) NOT NULL, "
                + "numOfSeats INT NOT NULL"
                + ");";

        try (PreparedStatement dbStmt = conn.prepareStatement(sql)) {
            dbStmt.execute();
            return true;
        } catch (SQLException sqle) {
            logger.error("Failed to initialize Lab table", sqle);
        } catch (Exception e) {
            logger.error("Unexpected error while initializing Lab table", e);
        }
        return false;
    }

    // CREATE LAB RECORD IN DB
    public boolean saveLab(Lab lab) {
        String sql = "INSERT INTO `Lab` "
                + "(labID, name, roomName, building, floor, campus, numOfSeats) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lab.getLabID());
            pstmt.setString(2, lab.getName());
            pstmt.setString(3, lab.getLocation().getRoomName());
            pstmt.setString(4, lab.getLocation().getBuilding());
            pstmt.setInt(5, lab.getLocation().getFloor());
            pstmt.setString(6, lab.getLocation().getCampus());
            pstmt.setInt(7, lab.getNumOfSeats());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab saved successfully: {}", lab.getLabID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while inserting lab", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while inserting lab", e);
        }
        return false;
    }

    public Lab getLabById(String labID) {
        String query = "SELECT * FROM `Lab` WHERE labID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, labID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Retrieved lab by ID: {}", labID);
                    return mapResultSetToLab(rs);
                }
            }

            logger.warn("Lab not found for ID: {}", labID);
        } catch (SQLException e) {
            logger.error("SQLException while fetching lab", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching lab", e);
        }
        return null;
    }

    public List<Lab> getLabsByName(String name) {
        String query = "SELECT * FROM `Lab` WHERE name = ? ORDER BY labID";
        List<Lab> labs = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    labs.add(mapResultSetToLab(rs));
                }
            }

            logger.info("Retrieved {} labs with name: {}", labs.size(), name);
        } catch (SQLException e) {
            logger.error("SQLException while fetching labs by name: {}", name, e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching labs by name: {}", name, e);
        }

        return labs;
    }

    public List<Lab> getAllLabs() {
        String query = "SELECT * FROM `Lab` ORDER BY labID";
        List<Lab> labs = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                labs.add(mapResultSetToLab(rs));
            }

            logger.info("Retrieved {} labs from database", labs.size());
        } catch (SQLException e) {
            logger.error("SQLException while fetching all labs", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while fetching all labs", e);
        }

        return labs;
    }

    public boolean updateLab(Lab updatedLab) {
        String query = "UPDATE `Lab` SET "
                + "name = ?, "
                + "roomName = ?, "
                + "building = ?, "
                + "floor = ?, "
                + "campus = ?, "
                + "numOfSeats = ? "
                + "WHERE labID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedLab.getName());
            pstmt.setString(2, updatedLab.getLocation().getRoomName());
            pstmt.setString(3, updatedLab.getLocation().getBuilding());
            pstmt.setInt(4, updatedLab.getLocation().getFloor());
            pstmt.setString(5, updatedLab.getLocation().getCampus());
            pstmt.setInt(6, updatedLab.getNumOfSeats());
            pstmt.setString(7, updatedLab.getLabID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab updated successfully: {}", updatedLab.getLabID());
            } else {
                logger.warn("No lab updated for ID: {}", updatedLab.getLabID());
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while updating lab", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while updating lab", e);
        }
        return false;
    }

    public boolean deleteLab(String labID) {
        String query = "DELETE FROM `Lab` WHERE labID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, labID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Lab deleted successfully: {}", labID);
            } else {
                logger.warn("No lab deleted for ID: {}", labID);
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("SQLException while deleting lab", e);
        } catch (Exception e) {
            logger.error("Unexpected exception while deleting lab", e);
        }
        return false;
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

        Location location = new Location();
        location.setRoomName(roomName);
        location.setBuilding(building);
        location.setFloor(floor);
        location.setCampus(campus);

        Lab lab = new Lab();
        lab.setLabID(labID);
        lab.setName(name);
        lab.setLocation(location);
        lab.setNumOfSeats(numOfSeats);

        return lab;
    }
}