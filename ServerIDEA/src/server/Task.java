package server;

public class Task {
    private String type;
    private String message;
    private Client client;
    public Task(Client c, String type, String message){
        this.client = c;
        this.type = type;
        this.message = message;
    }
    public Client getClient(){
        return client;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
