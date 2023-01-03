package server;

import client.ClientIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.Socket;
import java.util.Arrays;

public class Controller implements Runnable{
    private Socket socket;
    private DataStorage dataStorage;
    private ControllerIOManager controllerIOManager;
    private ServerLogika serverLogika;

    public Controller(Socket socket, DataStorage dataStorage, ServerLogika serverLogika){
        this.socket = socket;
        this.dataStorage = dataStorage;
        this.serverLogika = serverLogika;
        this.controllerIOManager = new ControllerIOManager(this.socket);
    }


    //ВТОРОЙ КОНСТРУТОР - ТОЛЬКО ДЛЯ ТЕСТИРОВАНИЯ
    public Controller(DataStorage dataStorage, ServerLogika serverLogika){
        this.dataStorage = dataStorage;
        this.serverLogika = serverLogika;
    }


    public void controller(){
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer());
        String str = controllerIOManager.readTextDataFromClient();
        Request request =  gsonBuilder.create().fromJson(str, Request.class);

        /*
        Использовалось для тестирования

        String str = new client.ClientIO().getRequestFromFile("testGet1.json").generateJSON();
        Request request =  gsonBuilder.create().fromJson(str, Request.class);
        */
        if (request.key != null && request.key.startsWith("[")){
            String [] keys = request.key.substring(1, request.key.length()-1).replaceAll(",","").split(" ");
            request.key = keys[0];
            request.keys = Arrays.copyOfRange(keys, 1 , keys.length);
        }

        String operation = request.type;

        switch (operation){
            case "get":
                sendDataToClient(request);
                break;
            case "set":
                writeDataFromClient(request);
                break;
            case "delete":
                deleteData(request);
                break;
            case "exit":
                exit();
                break;
            default:
                break;
        }
        controllerIOManager.closeStrims();
    }

    public void sendDataToClient(Request request){
        String str = dataStorage.get(request);
        controllerIOManager.sentDataToClient(str);
    }

    public void writeDataFromClient(Request request){
        String str = dataStorage.set(request);
        controllerIOManager.sentDataToClient(str);
    }

    public void deleteData(Request request){
        controllerIOManager.sentDataToClient(dataStorage.delete(request));
    }

    public void exit(){
        controllerIOManager.sentDataToClient(new Gson().toJson(new Response("OK", null, null)));
        serverLogika.stopMarker = false;
    }

    @Override
    public void run() {
        controller();
    }
}
