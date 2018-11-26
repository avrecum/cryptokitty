package commands;
import server.*;
public class CreateRoom {
    private Server server;
    public CreateRoom(Server server){
        this.server = server;
    }
    public void createroom(Client client, String s){
        String[] split = s.split(" ");
        if(split.length>1){
            String identifier = split[0];
            String password = split[1];
            if(server.getRoom(identifier)==null) {
                server.createRoom(identifier, password);
                client.sendLine(client.encrypt("Created room "+ identifier+ " with password "+ password+ "."));
            }else{
                client.sendLine(client.encrypt("Can't create room: Already exists."));
            }
        }else if(split.length >0){
            String identifier = split[0];
            String password = "";
            if(server.getRoom(identifier)==null){
                server.createRoom(identifier, "");
                client.sendLine(client.encrypt("Created room "+ identifier+"."));
            }else{
                client.sendLine(client.encrypt("Can't create room: Already exists."));
            }

        }
        else{
            client.sendLine(client.encrypt("Can't create room: no identifier specified."));
        }
    }
}
