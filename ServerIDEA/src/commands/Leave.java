package commands;
import server.*;

public class Leave {
    private Server server;
    public Leave(Server server){
        this.server = server;
    }
    public void leave(Client client, String s){
        String[] split = s.split(" ");
        if(split.length>0){
            String identifier = split[0];
            ChatRoom room = server.getRoom(identifier);
            if(room != null){
                if(room.isClientInRoom(client.hashCode())) {
                    room.removeClient(client.hashCode());
                    client.sendLine(client.encrypt("Left room "+ room.getName() + "."));
                }
                else{
                    client.sendLine(client.encrypt("Can't leave room: You are not part of the room."));
                }
            }else{
                client.sendLine(client.encrypt("Can't leave room: Room does not exist."));
            }
        }else{
            client.sendLine(client.encrypt("Can't leave room: No identifier specified."));
        }

    }
}
