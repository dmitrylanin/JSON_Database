package client;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Arrays;

public class RequestDeserializer implements JsonDeserializer<Request> {

    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonType = jsonObject.get("type");
        JsonElement jsonKey = jsonObject.get("key");
        JsonElement jsonValue = jsonObject.get("value");

        Request request = new Request();

        if(jsonType !=null){
            request.setType(jsonType.getAsString());
        }

        if(jsonKey != null && !jsonKey.isJsonNull() && jsonKey.isJsonArray()){
            request.setKey(convertJSONArrToStringArr(jsonKey.getAsJsonArray()));
        }else if(jsonKey != null && !jsonKey.isJsonNull()){
            request.setKey(jsonKey.getAsString());
        }

        if(jsonValue!= null && !jsonValue.isJsonNull() && jsonValue.isJsonObject()){
            request.setValue(jsonValue.getAsJsonObject().toString());
        }else if(jsonValue!= null && !jsonValue.isJsonNull()){
            request.setValue(jsonValue.getAsString());
        }

        request.requestJSON = request.generateJSON();
        return request;
    }

    private static String convertJSONArrToStringArr(JsonArray jsonArray){
        String [] arr = new String[jsonArray.size()];
        for (int i=0; i<jsonArray.size(); i++){
            arr[i] = jsonArray.get(i).getAsString();
        }
        return Arrays.toString(arr);
    }
}