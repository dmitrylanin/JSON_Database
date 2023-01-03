package server;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;

public class Response {

    @Expose
    public String response;
    @Expose
    public String reason;
    @Expose
    public String value;


    public Response(String response, String reason, String value){
        this.response = response;
        this.reason = reason;
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