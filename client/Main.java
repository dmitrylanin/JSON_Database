package client;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(100);

        Params params = new Params();
        JCommander.newBuilder()
                .addObject(params)
                .build()
                .parse(args);

        ClientIO clientIO = new ClientIO(params);
    }
}