package server.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(Server.class);
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
            logger.info("Starting server on port {}", PORT);
            serverSocket = new ServerSocket(PORT);
            logger.info("Server started successfully on port {}", PORT);
        } catch (IOException ioe) {
            logger.error("Failed to start server on port {}", PORT, ioe);
        }
    }



    private void waitForRequest(){
        try {
            logger.info("Waiting for client requests...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                logger.info("Accepted new client connection from {}", clientSocket.getInetAddress());
                Thread clientThread = new Thread(new ClientHandler(clientSocket, dbUtil.getDBConn()));
                clientThread.start();
            }
        } catch (IOException ioe) {
            logger.error("IOException while waiting for request", ioe);

        } catch (Exception e) {
            logger.error("Unexpected exception in waitForRequest", e);

        }
    }


    public static void main(String[] args) {
        new Server();
    }

}
