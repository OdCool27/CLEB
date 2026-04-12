package server.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.dispatcher.RequestDispatcher;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.sql.Connection;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RequestDispatcher dispatcher;
    private Connection dbConn;


    public ClientHandler(Socket socket, Connection dbConn) {
        this.socket = socket;
        this.dbConn = dbConn;
        this.dispatcher = new RequestDispatcher(dbConn);
    }

    private void closeConnections(){
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            logger.info("Closed connections for client {}", socket.getInetAddress());
        }catch (IOException ioe){
            logger.error("Error closing client connections", ioe);
        }
    }

    private void configureStreams(){
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            logger.debug("Streams configured for client {}", socket.getInetAddress());
        }catch (IOException ioe){
            logger.error("Error configuring streams for client {}", socket.getInetAddress(), ioe);
        }
    }

    @Override
    public void run() {
        try{
            configureStreams();

            while(true){
                try{

                    RequestEnvelope<?> request = (RequestEnvelope<?>) in.readObject();
                    logger.info("Received request: {} from client {}", request.getAction(), socket.getInetAddress());
                    ResponseEnvelope<?> response = dispatcher.dispatch(request, dbConn);
                    out.writeObject(response);
                    out.flush();
                    logger.info("Sent response: {} to client {}", response.getStatus(), socket.getInetAddress());

                }catch (ClassNotFoundException cnfe) {
                    logger.error("ClassNotFoundException while reading request", cnfe);

                }catch (ClassCastException ex) {
                    logger.error("ClassCastException while reading request", ex);
                }

            }
        }catch(EOFException eof){
            logger.info("Client {} disconnected", socket.getInetAddress());
        }catch(IOException ioe){
            logger.error("IOException in ClientHandler run loop", ioe);
        }catch(Exception e){
            logger.error("Unexpected exception in ClientHandler run loop", e);
        }
        closeConnections();
    }
}
