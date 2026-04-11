package server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final Logger logger = LogManager.getLogger(DBUtil.class);
    private Connection dbConn;
    private final String URL = "jdbc:mysql://localhost:3307/cleb_db";
    private final String USER = "root";
    private final String PASSWORD = "usbw";

    public DBUtil(){
        logger.info("Initializing DBUtil - connecting to database");
        this.dbConn = getDatabaseConnection();
    }

    public Connection getDBConn(){
        return dbConn;
    }

    public void closeConnection() throws SQLException{
        try {
            dbConn.close();
            logger.info("Database connection closed successfully");
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
            throw e;
        }
    }


    public Connection getDatabaseConnection(){

        if (dbConn == null){
            try{
                logger.debug("Attempting to connect to database at URL: {}", URL);
                dbConn = DriverManager.getConnection(URL, USER, PASSWORD);

                logger.info("Successfully connected to database");
                JOptionPane.showMessageDialog(null, "Connected to database successfully!", "DB Status",  JOptionPane.INFORMATION_MESSAGE);

                //Initializes all tables after the database is connected.
                if(!DatabaseInitializer.initializeAllTables(dbConn)){
                    System.exit(0);//Stops the server
                }

                return dbConn;

            }catch(SQLException sqle){
                logger.error("SQLException while connecting to database at URL: {}", URL, sqle);
                sqle.printStackTrace();
            }catch(Exception e){
                logger.error("Unexpected exception while connecting to database", e);
                e.printStackTrace();
            }
        }
        return dbConn;
    }



}
