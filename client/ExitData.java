package client;

public class ExitData implements Command{
    private Request request;

    public ExitData(Request request){
        this.request = request;
    }

    @Override
    public void execute() {
        request.doExit();
    }
}
