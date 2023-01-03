package client;

public class DeleteData implements Command{
    private Request request;

    public DeleteData(Request request){
        this.request = request;
    }

    @Override
    public void execute() {
        request.doDeleteData();
    }
}
