package client;

import com.beust.jcommander.Parameter;

public class Params {
    @Parameter(names = "-t")
    public String type;

    @Parameter(names = "-v")
    public String value;

    @Parameter(names = "-k")
    public String key;

    @Parameter(names = "-in")
    public String fileName;

    public Request createRequest(){
        return new Request(type, key, value);
    }
}
