package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.*;


public class Request {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ClientIO clientIO;
    public String requestJSON;

    @Expose
    public String type;
    @Expose
    public String value;
    @Expose
    public String key;

    Request(){};

    Request(String type, String key, String value){
        this.type = type;
        this.key = key;
        this.value = value;
        this.requestJSON = generateJSON();
    }

    public void doGetData(){
        try {
            dataOutputStream.writeUTF(requestJSON);
            System.out.println("Sent: " + requestJSON);
            String data = dataInputStream.readUTF();
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Response.class, new ResponseDeserializer());
            Response response = gsonBuilder.create().fromJson(data, Response.class);
            System.out.println("Received: " + response.generateJSON());


            //System.out.println("Received: " + response.value);
            clientIO.closeStrims();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void doSetData(){
        try {
            dataOutputStream.writeUTF(requestJSON);
            System.out.println("Sent: " + requestJSON);
            System.out.println("Received: " + dataInputStream.readUTF());
            clientIO.closeStrims();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void doDeleteData(){
        try {
            dataOutputStream.writeUTF(requestJSON);
            System.out.println("Sent: " + requestJSON);
            System.out.println("Received: " + dataInputStream.readUTF());
            clientIO.closeStrims();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void doExit(){
        try {
            dataOutputStream.writeUTF(requestJSON);
            System.out.println("Sent: " + requestJSON);
            System.out.println("Received: " + dataInputStream.readUTF());
            clientIO.closeStrims();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String generateJSON(){
        Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestSerializer()).create();
        return gson.toJson(this);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setClientIO(ClientIO clientIO) {
        this.clientIO = clientIO;
        try {
            this.dataInputStream = new DataInputStream(clientIO.socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(clientIO.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}