package client;

import com.google.gson.*;
import java.lang.reflect.Type;

public class Response {
    public String reason;
    public String value;
    public String response;

    Response(){};

    Response(String reason, String value, String respose){
        this.reason = reason;
        this.value = value;
        this.response = respose;
    }

    public void setStrResponse(String respose) {
        this.response = respose;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String generateJSON(){
        Gson gson = new GsonBuilder().registerTypeAdapter(Response.class, resposeSerializer).create();
        return gson.toJson(this);
    }

    private JsonSerializer resposeSerializer = new JsonSerializer<Response>(){
        @Override
        public JsonElement serialize(Response src, Type type, JsonSerializationContext jsonSerializationContext){
            JsonObject responseJsonO = new JsonObject();
            responseJsonO.addProperty("response", src.response);

            if(src.reason != null){
                responseJsonO.addProperty("reason", src.reason);
            }

            if(src.value != null){
                try {
                    JsonObject jsonValueElement = JsonParser.parseString(src.value).getAsJsonObject();
                    responseJsonO.add("value", jsonValueElement);
                }catch (JsonSyntaxException e){
                    responseJsonO.addProperty("value", src.value);
                }
            }
            return responseJsonO;
        };
    };
}
