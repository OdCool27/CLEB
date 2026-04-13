package server.networking;

import dispatcher.RequestDispatcher;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.io.ObjectStreamClass;
import java.net.Socket;
import java.net.SocketException;
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
            in = new CompatibleObjectInputStream(socket.getInputStream());
            logger.debug("Streams configured for client {}", socket.getInetAddress());
        }catch (IOException ioe){
            logger.error("Error configuring streams for client {}", socket.getInetAddress(), ioe);
        }
    }

    public synchronized boolean sendResponse(ResponseEnvelope<?> response) {
        try {
            if (out == null) {
                return false;
            }
            out.writeObject(response);
            out.flush();
            return true;
        } catch (IOException ioe) {
            logger.error("Error sending response to client {}", socket.getInetAddress(), ioe);
            return false;
        }
    }

    /**
     * Helps the server read serialized objects even when the runtime classloader
     * or older package names differ from the current source layout.
     */
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
                            return Class.forName(candidate, false, ClientHandler.class.getClassLoader());
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

    @Override
    public void run() {
        try{
            configureStreams();
            ClientRegistry.register(this);

            while(true){
                try{

                    RequestEnvelope<?> request = (RequestEnvelope<?>) in.readObject();
                    logger.info("Received request: {} from client {}", request.getAction(), socket.getInetAddress());
                    ResponseEnvelope<?> response = dispatcher.dispatch(request, dbConn);
                    sendResponse(response);
                    logger.info("Sent response: {} to client {}", response.getStatus(), socket.getInetAddress());

                }catch (ClassNotFoundException cnfe) {
                    logger.error("ClassNotFoundException while reading request", cnfe);

                }catch (ClassCastException ex) {
                    logger.error("ClassCastException while reading request", ex);

                }catch (SocketException se) {
                    logger.info("Client {} triggered a SocketException: " + se.getMessage(), socket.getInetAddress());
                    break;

                }catch (EOFException eofe) {
                    logger.info("Client {} connection reset/closed:" + eofe.getMessage(), socket.getInetAddress());
                    break;
                }

            }
        }catch(EOFException eof){
            logger.info("Client {} disconnected", socket.getInetAddress());
        }catch(IOException ioe){
            logger.error("IOException in ClientHandler run loop", ioe);
        }catch(Exception e){
            logger.error("Unexpected exception in ClientHandler run loop", e);
        }finally{
            ClientRegistry.unregister(this);
            closeConnections();
        }
    }
}
