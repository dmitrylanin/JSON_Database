package client;

public class SetData implements Command{
    private Request request;

    public SetData(Request request){
        this.request = request;
    }

    @Override
    public void execute() {
        request.doSetData();
    }
}
