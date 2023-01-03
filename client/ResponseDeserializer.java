package client;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ResponseDeserializer implements JsonDeserializer<Response> {

    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonStrResponce = jsonObject.get("response");
        JsonElement jsonReason = jsonObject.get("reason");
        JsonElement jsonValue = jsonObject.get("value");

        Response response = new Response();

        response.setStrResponse(jsonStrResponce.getAsString());

        if(jsonReason !=null){
            response.setReason(jsonReason.getAsString());
        }

        if(jsonValue !=null && jsonValue.isJsonObject()){
            response.setValue(jsonValue.getAsJsonObject().getAsString());
        }else if(jsonValue !=null){

            response.setValue(jsonValue.getAsString());
        }

        return response;
    }
}
