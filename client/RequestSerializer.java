package client;

import com.google.gson.*;
import java.lang.reflect.Type;

public class RequestSerializer implements JsonSerializer<Request> {

    @Override
    public JsonElement serialize(Request src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject requestJsonObj = new JsonObject();
        requestJsonObj.addProperty("type", src.type);

        if(src.key != null){
            JsonElement jsonElement = JsonParser.parseString(src.key);
            if(jsonElement.isJsonArray()){
                requestJsonObj.add("key", jsonElement.getAsJsonArray());
            }else if(jsonElement.isJsonObject()){
                requestJsonObj.add("key", jsonElement.getAsJsonObject());
            }else{
                requestJsonObj.addProperty("key", src.key);
            }
        }


        if(src.value != null){
            try {
                JsonElement jsonValue = JsonParser.parseString(src.value);
                if(jsonValue.isJsonObject()){
                    requestJsonObj.add("value", jsonValue.getAsJsonObject());
                }else if(jsonValue.isJsonArray()){
                    requestJsonObj.add("value", jsonValue.getAsJsonArray());
                }else{
                    requestJsonObj.addProperty("value", src.value);
                }

            }catch (JsonSyntaxException e){
                requestJsonObj.addProperty("value", src.value);
            }
        }

        return requestJsonObj;
    }
}
