package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ControllerIOManager{
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;

    ControllerIOManager(Socket socket){
        this.socket = socket;
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readTextDataFromClient(){
        try {
            return new String(dataInputStream.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sentDataToClient(String str){
        try {
            dataOutputStream.writeUTF(str);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeStrims(){
        try {
            if(!socket.isClosed()){
                socket.getOutputStream().close();
            }
            if(!socket.isClosed()){
                socket.getInputStream().close();
            }
            if(!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}