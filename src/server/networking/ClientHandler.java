package server.networking;

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
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RequestDispatcher dispatcher;
    private Connection dbConn;


    public ClientHandler(Socket socket, Connection dbConn) {
        this.socket = socket;
        this.dbConn = dbConn;
        this.dispatcher = new RequestDispatcher();
    }

    private void closeConnections(){
        try {
            out.close();
            in.close();
            socket.close();
        }catch (IOException ioe){
            ioe.printStackTrace();//replace with throw or log
        }
    }

    private void configureStreams(){
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }catch (IOException ioe){
            ioe.printStackTrace();//replace with throw or log
        }
    }

    @Override
    public void run() {
        try{
            configureStreams();

            while(true){
                try{

                    RequestEnvelope<?> request = (RequestEnvelope<?>) in.readObject();
                    ResponseEnvelope<?> response = dispatcher.dispatch(request, dbConn);
                    out.writeObject(response);
                    out.flush();

                }catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();

                }catch (ClassCastException ex) {
                    ex.printStackTrace();
                }

            }
        }catch(EOFException eof){
            eof.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        closeConnections();
    }
}
