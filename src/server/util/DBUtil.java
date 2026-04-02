package server.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private Connection dbConn;
    private final String URL = "jdbc:mysql://localhost:3307/dbLab07";
    private final String USER = "root";
    private final String PASSWORD = "usbw";

    public DBUtil(){
        this.dbConn = getDatabaseConnection();
    }

    public Connection getDBConn(){
        return dbConn;
    }

    public void closeConnection() throws SQLException{
        dbConn.close();
    }


    public Connection getDatabaseConnection(){

        if (dbConn == null){
            try{
                dbConn = DriverManager.getConnection(URL, USER, PASSWORD);

                JOptionPane.showMessageDialog(null, "Connected to database successfully!", "DB Status",  JOptionPane.INFORMATION_MESSAGE);

                return dbConn;

            }catch(SQLException sqle){
                sqle.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return dbConn;
    }
}
