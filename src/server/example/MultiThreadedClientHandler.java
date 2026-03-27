package server.example;

import server.dispatcher.RequestDispatcher;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

/**
 * Example of a Multi-threaded Client Handler.
 * This class handles one client connection concurrently and uses RequestDispatcher to process requests.
 */
public class MultiThreadedClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Connection dbConn;
    private RequestDispatcher dispatcher;

    public MultiThreadedClientHandler(Socket socket, Connection dbConn, RequestDispatcher dispatcher) {
        this.socket = socket;
        this.dbConn = dbConn;
        this.dispatcher = dispatcher;
    }

    private void configureStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void closeConnections() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ioe) {
            System.err.println("Error closing connection for client " + socket.getInetAddress() + ": " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            configureStreams();

            while (true) {
                try {
                    // Based on existing Server and ClientHandler structure
                    // The original Server.java only reads RequestEnvelope
                    // The original ClientHandler.java attempted to read a String (action) and then a RequestEnvelope
                    
                    Object input = in.readObject();
                    if (input instanceof RequestEnvelope<?> request) {
                        ResponseEnvelope<?> response = dispatcher.dispatch(request);
                        out.writeObject(response);
                        out.flush();
                    } else if (input instanceof String action) {
                        // Handle legacy or specific string actions if necessary
                        System.out.println("Received action string: " + action);
                        // Original ClientHandler expected a RequestEnvelope after action
                        Object nextInput = in.readObject();
                        if (nextInput instanceof RequestEnvelope<?> request) {
                            ResponseEnvelope<?> response = dispatcher.dispatch(request);
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                } catch (ClassNotFoundException cnfe) {
                    System.err.println("Class not found during object reading: " + cnfe.getMessage());
                } catch (EOFException eof) {
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                    break;
                }
            }
        } catch (IOException ioe) {
            System.err.println("IO Error handling client " + socket.getInetAddress() + ": " + ioe.getMessage());
        } finally {
            closeConnections();
        }
    }
}
