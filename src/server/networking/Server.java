package server.networking;

import server.dispatcher.RequestDispatcher;
import server.dto.StudentDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.User;
import server.util.DBUtil;

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
    private DBUtil dbUtil;
    private static final int PORT = 49494;



    public Server() {
        dbUtil = new DBUtil();
        createConnection();
        waitForRequest();
    }


    private void createConnection(){
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    private void waitForRequest(){
        try {
            while(true){
                Thread clientThread = new Thread(new ClientHandler(serverSocket.accept(), dbUtil.getDBConn()));
                clientThread.start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public static void main(String[] args) {
        new Server();
    }

}
