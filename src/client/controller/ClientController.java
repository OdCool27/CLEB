package controller;

import dto.LoginRequestDTO;
import dto.ReservationDTO;
import dto.StudentDTO;
import dto.UserDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.SwingUtilities;

public class ClientController {
    private static final Logger logger = LogManager.getLogger(ClientController.class);
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final BlockingQueue<ResponseEnvelope<?>> responseQueue = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<Runnable> reservationUpdateListeners = new CopyOnWriteArrayList<>();
    private volatile boolean listening;
    private Thread listenerThread;

    public ClientController(){
        createConnection();
        configureStreams();
    }


    //CONNECTS TO SERVER
    private void createConnection(){
        logger.info("Attempting to connect to server at 127.0.0.1:49494");
        try {
            //Creates a socket to connect to server
            socket = new Socket("127.0.0.1", 49494); //LOCAL HOST + SERVER PORT
            logger.info("Successfully connected to server.");
        }catch(IOException ioe) {
            logger.error("Failed to connect to server: {}", ioe.getMessage());
            ioe.printStackTrace();
        }
    }


    //SETUP OBJECT STREAMS
    private void configureStreams(){
        try {
            logger.debug("Configuring object streams...");
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new CompatibleObjectInputStream(socket.getInputStream());
            startResponseListener();
            logger.debug("Object streams configured successfully.");
        }catch(IOException ioe) {
            logger.error("Error configuring object streams: {}", ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public void shutdown() {
        listening = false;
        closeConnection();
    }

    private void closeConnection(){
        try {
            logger.info("Closing server connection...");
            if (oos != null) {
                oos.close();
            }
            if (ois != null) {
                ois.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            logger.info("Connection closed.");
        }catch(IOException ioe) {
            logger.error("Error while closing connection: {}", ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    //SEND Generic Request Envelope
    public void sendRequest(RequestEnvelope<?> envelope) {
        try {
            logger.info("Sending request: {} - Action: {}", envelope.getCorrelationId(), envelope.getAction());
            oos.writeObject(envelope);
            oos.flush();
        } catch (IOException ioe) {
            logger.error("Error sending request: {}", ioe.getMessage());
        }
    }

    //RECEIVE Generic Response Envelope
    @SuppressWarnings("unchecked")
    public <T> ResponseEnvelope<T> receiveResponse() {
        try {
            logger.debug("Waiting for response...");
            ResponseEnvelope<?> envelope = responseQueue.take();
            logger.info("Received response: {} - Status: {}", envelope.getCorrelationId(), envelope.getStatus());
            return (ResponseEnvelope<T>) envelope;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error receiving response", e);
        }
        return null;
    }

    public void addReservationUpdateListener(Runnable listener) {
        reservationUpdateListeners.add(listener);
    }

    public void removeReservationUpdateListener(Runnable listener) {
        reservationUpdateListeners.remove(listener);
    }

    public ResponseEnvelope<UserDTO> handleLoginResponse() {
        return receiveResponse();
    }


    public ResponseEnvelope<List<?>> handleReservationResponse() {
        return receiveResponse();
    }

    private void startResponseListener() {
        listening = true;
        listenerThread = new Thread(() -> {
            while (listening) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ResponseEnvelope<?> envelope) {
                        if ("PUSH".equalsIgnoreCase(envelope.getStatus())
                                && "RESERVATION_UPDATED".equals(envelope.getPayload())) {
                            notifyReservationListeners();
                        } else {
                            responseQueue.offer(envelope);
                        }
                    }
                } catch (ClassNotFoundException | IOException e) {
                    if (listening) {
                        logger.error("Error in response listener", e);
                    }
                    break;
                }
            }
        }, "client-controller-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void notifyReservationListeners() {
        for (Runnable listener : reservationUpdateListeners) {
            SwingUtilities.invokeLater(listener);
        }
    }





    private static class CompatibleObjectInputStream extends ObjectInputStream {
        CompatibleObjectInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String className = desc.getName();

            try {
                return super.resolveClass(desc);
            } catch (ClassNotFoundException ignored) {
                for (String candidate : remapCandidates(className)) {
                    try {
                        return Class.forName(candidate, false, Thread.currentThread().getContextClassLoader());
                    } catch (ClassNotFoundException ignoredAgain) {
                        try {
                            return Class.forName(candidate, false, ClientController.class.getClassLoader());
                        } catch (ClassNotFoundException ignoredOnceMore) {
                            // Keep trying candidates.
                        }
                    }
                }
                throw new ClassNotFoundException(className);
            }
        }

        private String[] remapCandidates(String className) {
            if (className.startsWith("server.")) {
                return new String[]{className, className.substring("server.".length())};
            }
            return new String[]{className, "server." + className};
        }
    }
}

