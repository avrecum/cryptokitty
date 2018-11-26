package commands;
import server.*;

public class Join {
    private Server server;
    public Join(Server server){
        this.server = server;
    }
    public void join(Client client, String s){
        String[] parts = s.split(" ");
        String identifier = parts[0];
        String password;
        if(parts.length >1){
            password = parts[1];
        }
        else{
            password = "";
        }
        if(parts[0]==null) identifier="";
        ChatRoom room = server.getRoom(identifier);
        if(room != null){
            if(room.comparePassword(password)){
                //join room
                System.out.println("joining room");
                room.addClient(client.hashCode());
                System.out.println("joined room");
            }else{
                //wrong password
            }
        }else{
            //room doesn't exist;
        }
    }
}
