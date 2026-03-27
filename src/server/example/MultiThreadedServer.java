package server.example;

import server.dispatcher.RequestDispatcher;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example of a Multi-threaded Server based on existing Server class logic.
 * This class uses a thread pool to handle concurrent client connections.
 */
public class MultiThreadedServer {
    private ServerSocket serverSocket;
    private Connection dbConn;
    private RequestDispatcher dispatcher;
    private static final int PORT = 49494;
    private static final int THREAD_POOL_SIZE = 30; // Based on README (10-30 clients)
    private ExecutorService threadPool;

    public MultiThreadedServer() {
        this.dispatcher = new RequestDispatcher();
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        initializeDatabase();
        startServer();
    }

    private void initializeDatabase() {
        if (dbConn == null) {
            try {
                // Connection details from original Server class
                String url = "jdbc:mysql://localhost:3307/dbLab07";
                String username = "root";
                String password = "usbw";
                dbConn = DriverManager.getConnection(url, username, password);
                
                System.out.println("Connected to database successfully!");
            } catch (SQLException sqle) {
                System.err.println("Database connection failed: " + sqle.getMessage());
            }
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Multi-threaded Server started on port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    // Hand off client to a worker thread from the pool
                    MultiThreadedClientHandler handler = new MultiThreadedClientHandler(clientSocket, dbConn, dispatcher);
                    threadPool.execute(handler);
                } catch (IOException ioe) {
                    System.err.println("Error accepting client connection: " + ioe.getMessage());
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (dbConn != null && !dbConn.isClosed()) {
                dbConn.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MultiThreadedServer();
    }
}
