package server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerLogika{
    protected boolean stopMarker = true;
    private DataStorage dataStorage;
    private Controller controller;
    private ServerSocket serverSocket;

    public ServerLogika(){
            System.out.println("Server started!");
            this.dataStorage = new DataStorage();
            try {
                this.serverSocket = new ServerSocket(23456, 50, InetAddress.getByName("127.0.0.1"));
            }catch (Exception e) {};

    }



    public void serverListening(){
        while (stopMarker){
            try{
                Socket socket = serverSocket.accept();
                this.controller = new Controller(socket, dataStorage, this);
                controller.run();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            new RandomAccessFile(dataStorage.getDataFile(), "rw").setLength(0);
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
