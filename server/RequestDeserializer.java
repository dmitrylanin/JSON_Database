package server;

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

        Request rq = new Request();

        if(jsonType !=null){
            rq.setType(jsonType.getAsString());
        }

        if(jsonKey != null && !jsonKey.isJsonNull() && jsonKey.isJsonArray()){
            rq.setKey(convertJSONArrToStringArr(jsonKey.getAsJsonArray()));
        }else if(jsonKey != null && !jsonKey.isJsonNull()){
            rq.setKey(jsonKey.getAsString());
        }

        if(jsonValue!= null && !jsonValue.isJsonNull() && jsonValue.isJsonObject()){
            rq.setValue(jsonValue.getAsJsonObject().toString());
        }else if(jsonValue!= null && !jsonValue.isJsonNull()){
            rq.setValue(jsonValue.getAsString());
        }

        return rq;
    }

    private static String convertJSONArrToStringArr(JsonArray jsonArray){
        String [] arr = new String[jsonArray.size()];
        for (int i=0; i<jsonArray.size(); i++){
            arr[i] = jsonArray.get(i).getAsString();
        }
        return Arrays.toString(arr);
    }
}
