package networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ReservationCompletionMonitor;
import util.DBUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private ServerSocket serverSocket;
    private DBUtil dbUtil;
    private ScheduledExecutorService scheduler;
    private static final int PORT = 49494;



    public Server() {
        dbUtil = new DBUtil();
        createConnection();
        startReservationMonitor();
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

    private void startReservationMonitor() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        ReservationCompletionMonitor monitor = new ReservationCompletionMonitor(dbUtil.getDBConn());

        scheduler.scheduleAtFixedRate(() -> {
            try {
                int completed = monitor.completeElapsedReservations();
                if (completed > 0) {
                    logger.info("Auto-completed {} reservations", completed);
                    ClientRegistry.broadcastReservationUpdate();
                }
            } catch (Exception e) {
                logger.error("Error while running reservation completion monitor", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
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
