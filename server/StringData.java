package server;

import com.google.gson.annotations.Expose;

public class StringData {
    @Expose
    public String key;
    @Expose
    public String value;

    public StringData(String key, String value){
        this.value =value;
        this.key = key;
    }
}
