package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataStorage {
    private HashMap<String, Request> cash;
    private File directory;
    private File dataFile;
    private ReadWriteLock readWriteLock;
    private Lock readLock;
    private Lock writeLock;

    DataStorage() {
        this.directory = new File(System.getProperty("user.dir")
                + File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator);
        if (!this.directory.exists()){
            this.directory.mkdirs();
        }
        this.dataFile = new File(this.directory + File.separator + "db.json");
        if (!this.dataFile.exists()){
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.cash = new HashMap<>();
        /*
        for (String key : cash.keySet()){
                    System.out.println("key " + key);
                    System.out.println(cash.get(key).generateJSON());
                }

         */

        loadDataToCash();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public String set(Request request){
        request.type = null;
        writeLock.lock();
        boolean containedMarker = false;
        ArrayList<Request> requestList = new ArrayList<>();
        //Считываем строку с JSON-ами из файла в массив
        try(BufferedReader reader = new BufferedReader(new FileReader(dataFile))){
            Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();
            Request requestArr[] = gson.fromJson(reader.readLine(), Request[].class);

                    //Если requestArr пуст - то есть, в файле ничего не было - добавляем первую запись - это полученный request
            if(requestArr == null){
                requestList.add(request);
            }else{
                    //Если в requestArr есть записи - создаем коллекцию из массива
                requestList = new ArrayList<>(Arrays.asList(requestArr));
                for (int i = 0; i < requestList.size(); i++) {
                    //Если на вход передали только 1 ключ
                    if (requestList.get(i).key.equals(request.key) && request.keys == null)
                    {
                        requestList.get(i).value = request.value;
                        containedMarker = true;
                    }
                    //Если на вход передали несколько ключей
                    else if (requestList.get(i).key.equals(request.key) && request.keys != null)
                    {
                        requestList.get(i).value = setMultipleData(JsonParser.parseString(requestList.get(i).value).getAsJsonObject(), request.keys, request.value).toString();
                        containedMarker = true;
                    }
                }
                    //Если переданного request не было в файле - это новый request и его нужно записать
                if (!containedMarker){
                    requestList.add(request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Записываем JSON-ы в файл
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, false))){
            Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestSerializer()).create();
            String finalJSON = gson.toJson(requestList, new TypeToken<List<Request>>(){}.getType());

            writer.write(finalJSON);
            for (int i=0; i<requestList.size(); i++){
                cash.put(requestList.get(i).key, requestList.get(i));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        writeLock.unlock();
        return new Gson().toJson(new Response("OK", null, null));
    }

    public JsonObject setMultipleData(JsonObject base, String [] keys, String newData){
        JsonElement jO = null;
        if(keys.length == 1){
            base.addProperty(keys[0], newData);
            return base;
        }else{
            jO = base.get(keys[0]);
            JsonObject forAdding = setMultipleData(jO.getAsJsonObject(), Arrays.copyOfRange(keys, 1, keys.length), newData);
            if(forAdding == null){
                return null;
            }else {
                return base;
            }
        }
    }

    public String get(Request baseRequest){
        readLock.lock();

        if(!cash.containsKey(baseRequest.key)){
            readLock.unlock();
            return new Gson().toJson(new Response("ERROR", "No such key", null));
        }else{
            Request data = cash.get(baseRequest.key);
            if(baseRequest.keys != null && baseRequest.keys.length>0){
                JsonObject json = JsonParser.parseString(data.generateJSON()).getAsJsonObject().get("value").getAsJsonObject();
                Response response = new Response("OK", null, getMultipleData(json, baseRequest.keys).getAsString());
                readLock.unlock();
                return new Gson().toJson(response);
            }else{
                //JsonObject proxyObj = JsonParser.parseString(data.generateJSON()).getAsJsonObject();
                JsonElement jsonValue = JsonParser.parseString(data.generateJSON()).getAsJsonObject().get("value").getAsJsonObject();
                readLock.unlock();
                return new Gson().toJson(new Response("OK", null, jsonValue.toString()));
            }
        }
    }

    public JsonElement getMultipleData(JsonObject base, String [] keys){
        if(keys.length == 1){
            return base.get(keys[0]);
        }else{
            JsonElement jO = base.get(keys[0]);
            jO = getMultipleData(jO.getAsJsonObject(), Arrays.copyOfRange(keys, 1, keys.length));
            if(jO == null){
                return null;
            }
        }
        return null;
    }

    public String delete(Request request){
        writeLock.lock();
        /*
            Читаем строку с JSON-ами из файла в массив
            Заменяем удаляемый массив null'ом
         */

        ArrayList<Request> requestDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(dataFile))){
            Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();
            Request [] requestArr = gson.fromJson(reader.readLine(), Request[].class);

            for (int i = 0; i<requestArr.length; i++){
                //Если массив ключей изначально был пустым и передали только 1 ключ
                if(requestArr[i].key.equals(request.key) && request.keys == null){
                    requestArr[i].value = null;
                }else if (requestArr[i].key.equals(request.key) && request.keys != null){
                    //Если на вход передали несколько ключей
                    requestArr[i].value = deleteMultipleData(JsonParser.parseString(requestArr[i].value).getAsJsonObject(), request.keys).toString();
                }
            }

            requestDataList = new ArrayList<>(Arrays.asList(requestArr));
            requestDataList.removeAll(Collections.singleton(null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Записываем собранную строку в базу
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, false))){
            String finalJSON = new Gson().toJson(requestDataList);
            writer.write(finalJSON);

            for (int i=0; i<requestDataList.size(); i++){
                cash.put(requestDataList.get(i).key, requestDataList.get(i));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLock.unlock();
        return new Gson().toJson(new Response("OK", null, null));
    }

    public static JsonObject deleteMultipleData(JsonObject base, String [] keys){
        JsonElement jO = null;
        if(keys.length == 1){
            base.remove(keys[0]);
            return base;
        }else{
            jO = base.get(keys[0]);
            JsonObject forAdding = deleteMultipleData(jO.getAsJsonObject(), Arrays.copyOfRange(keys, 1, keys.length));
            if(forAdding == null){
                return null;
            }else {
                return base;
            }
        }
    }

    public void loadDataToCash(){
        try(BufferedReader readerForCash = new BufferedReader(new FileReader(dataFile))){
            String data = readerForCash.readLine();
            if(data != null) {
                JsonArray requestArr = JsonParser.parseString(data).getAsJsonArray();
                GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer());
                //Если requestArr пуст - то есть, в файле ничего не было - добавляем первую запись - это полученный request
                if (requestArr != null) {
                    for (int i = 0; i < requestArr.size(); i++) {
                        Request request = gsonBuilder.create().fromJson(requestArr.get(i).getAsJsonObject(), Request.class);
                        cash.put(request.key, request);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public File getDataFile() {
        return dataFile;
    }

    private JsonSerializer responseSerializer = new JsonSerializer<Response>(){
        @Override
        public JsonElement serialize(Response src, Type type, JsonSerializationContext jsonSerializationContext){
            JsonObject responseJsonO = new JsonObject();
            responseJsonO.addProperty("response", src.response);

            if(src.reason != null){
                responseJsonO.addProperty("reason", src.reason);
            }

            if(src.value != null){
                JsonElement proxyJsonElement = JsonParser.parseString(src.value);
                if(proxyJsonElement.isJsonObject()){
                    responseJsonO.add("value", proxyJsonElement.getAsJsonObject());
                }else{
                    responseJsonO.addProperty("value", src.value);
                }
            }

            return responseJsonO;
        };
    };
}