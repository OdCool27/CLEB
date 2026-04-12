package client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientController 
{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    public ClientController() {
        getConnection();
        configureStreams();
    }

    // INSTANTIATE SOCKET ON SAME ADDRESS AND PORT
    public void getConnection() {
        try {
            socket = new Socket("127.0.0.1", 49494);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // CONFIG STREAMS
    public void configureStreams() {
        try {
            if (socket != null && !socket.isClosed()) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(socket.getInputStream());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
