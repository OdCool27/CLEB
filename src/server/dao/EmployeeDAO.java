package server.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeDAO {
    private Connection conn;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean initializeTable() {
        UserDAO userDAO = new UserDAO(conn);

        //First tries to initialize User Table before initializing Student Table
        if (userDAO.initializeTable()) {

            String sql = "CREATE TABLE Student ("
                    + "userID INT NOT NULL, "
                    + "studentID VARCHAR(20) NOT NULL UNIQUE, "
                    + "faculty VARCHAR(50) NOT NULL, "
                    + "school VARCHAR(50) NOT NULL, "
                    + "PRIMARY KEY (userID), "
                    + "FOREIGN KEY (userID) REFERENCES User(userID)"
                    + ");";

            try (Statement dbStmt = conn.createStatement()) {
                return dbStmt.execute(sql);
            } catch (SQLException sqle) {
                System.err.println("Failed to initialize User Table : " + sqle.getMessage());
                return false;
            }

        }else{
            return false;//returns false if the User table is not initialized
        }
    }
}
