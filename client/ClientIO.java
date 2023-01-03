package client;

import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientIO{
    protected Socket socket;
    private File directory;
    private Params paramsBlock;

    ClientIO(Params paramsBlock){
        try {
            this.socket = new Socket(InetAddress.getByName("127.0.0.1"), 23456);
            this.paramsBlock = paramsBlock;
            engine();
            closeStrims();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Этот конструктор нужно будет потом удалить
    public ClientIO(){};



    public void engine() throws IOException {
        Request request;
        Controller controller = new Controller();
        System.out.println("Client started!");
        String operation;

            //Ветвление - считываем параметры запроса из командной строки или из файла
        if(paramsBlock.fileName == null){
            //Формируем объект запроса из данных командной строки
            request = paramsBlock.createRequest();
            operation = paramsBlock.type;
        }else{
            //Получаем объект запроса через десериализацию данных из файла
            request = getRequestFromFile(paramsBlock.fileName);

            operation = request.type;
        }


        request.setClientIO(this);
        switch (operation){
            case "get":
                controller.setCommand(new GetData(request));
                controller.executeCommand();
                break;
            case "set":
                controller.setCommand(new SetData(request));
                controller.executeCommand();
                break;
            case "delete":
                controller.setCommand(new DeleteData(request));
                controller.executeCommand();
                break;
            case "exit":
                controller.setCommand(new ExitData(request));
                controller.executeCommand();
                break;
            default:
                break;
        }
    }

    public void closeStrims(){
        try{
            if(!socket.isClosed()){
                socket.getOutputStream().close();
            }
            if(!socket.isClosed()){
                socket.getInputStream().close();
            }
            if(!socket.isClosed()){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request getRequestFromFile(String fileName){
        /*
            Получаем название файла и считываем данные в строку
            Десериализуем строку и создаем объект Запрос
         */
        this.directory = new File(System.getProperty("user.dir")
                + File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator);
        if (!this.directory.exists()){
            this.directory.mkdirs();
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(this.directory + File.separator + fileName))){
            char[] buf = new char[1024];
            int numRead=0;
            String rJSON = "";
            StringBuffer fileData = new StringBuffer();
            while ((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            rJSON = fileData.toString();
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer());
            Request request = gsonBuilder.create().fromJson(rJSON, Request.class);
            return request;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}