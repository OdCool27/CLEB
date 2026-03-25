package server.networking;

import server.dispatcher.RequestDispatcher;
import server.dto.StudentDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.User;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Connection dbConn;
    private RequestDispatcher dispatcher;
    private static final int PORT = 49494;



    public Server() {
        createConnection();
        waitForRequest();

    }

    private void instantiateStreams(){
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private void createConnection(){
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private Connection getDatabaseConnection(){

        if (dbConn == null){
            try{
                String url = "jdbc:mysql://localhost:3307/dbLab07";
                String username = "root";
                String password = "usbw";
                dbConn = DriverManager.getConnection(url, username, password);

                JOptionPane.showMessageDialog(null, "Connected to database successfully!",
                        "DB Status",  JOptionPane.INFORMATION_MESSAGE);

                return dbConn;

            }catch(SQLException sqle){
                sqle.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return dbConn;
    }

    private void waitForRequest(){
        String action="";
        getDatabaseConnection();

        try {
            while(true){
                socket = serverSocket.accept();
                instantiateStreams();

                try{
                    RequestEnvelope<?> request = (RequestEnvelope<?>) ois.readObject();
                    ResponseEnvelope<?> response = dispatcher.dispatch(request, dbConn);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                closeConnection();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            oos.close();
            ois.close();
            dbConn.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}
