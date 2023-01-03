package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Request {
    public String type;
    public String value;
    public String key;
    public String [] keys;
    public String requestJSON;


    public Request (){};

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String generateJSON(){
        Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestSerializer()).create();
        return gson.toJson(this);
    }
}

