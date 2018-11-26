package server;

public class Message {
    private String message;
    private Client client;
    public Message(Client client, String message){
        this.client = client;
        this.message = message;
    }
    public Client getClient() {
        return this.client;
    }
    public String getMessage(){
        return this.message;
    }

    public static String getChatroomIdentifier(String message){
        int indexOf = message.indexOf(":");
        if(indexOf == -1) indexOf = 0;
        String b = message.substring(0, indexOf);
        return b;
    }
    public static String isolateMessage(String s, String command){
        int indexOf = s.indexOf(":");
        if(indexOf == -1) indexOf = 0;
        String a = s.substring(indexOf);
        String b = a.replace("!!" + command + " ", "");
        if (b == null) b = "";
        System.out.println(b);
        return b;
    }
}