package server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.dao.*;

import java.sql.Connection;

public class DatabaseInitializer {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

    public static boolean initializeAllTables(Connection conn) {

        try {
            // --- DAO INSTANCES ---
            UserDAO userDAO = new UserDAO(conn);
            StudentDAO studentDAO = new StudentDAO(conn);
            EmployeeDAO employeeDAO = new EmployeeDAO(conn);

            LabDAO labDAO = new LabDAO(conn);
            LabSeatDAO labSeatDAO = new LabSeatDAO(conn);
            EquipmentDAO equipmentDAO = new EquipmentDAO(conn);

            EquipmentReservationDAO equipmentReservationDAO = new EquipmentReservationDAO(conn);
            LabSeatReservationDAO labSeatReservationDAO = new LabSeatReservationDAO(conn);

            // --- INITIALIZATION ORDER ---

            // 1. Base tables
            if (!userDAO.initializeTable()) return false;
            if (!labDAO.initializeTable()) return false;

            // 2. User subtypes
            if (!studentDAO.initializeTable()) return false;
            if (!employeeDAO.initializeTable()) return false;

            // 3. Lab dependencies
            if (!labSeatDAO.initializeTable()) return false;
            if (!equipmentDAO.initializeTable()) return false;

            // 4. Reservations (depend on everything)
            if (!equipmentReservationDAO.initializeTable()) return false;
            if (!labSeatReservationDAO.initializeTable()) return false;

            logger.info("Successfully initialized tables");
            return true;

        } catch (Exception e) {
            logger.error("Failed to initialize tables", e);
            return false;
        }
    }
}
