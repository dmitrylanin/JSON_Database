package client;

public class GetData implements Command{
    private Request request;

    public GetData(Request request){
        this.request = request;
    }

    @Override
    public void execute() {
        request.doGetData();
    }
}
