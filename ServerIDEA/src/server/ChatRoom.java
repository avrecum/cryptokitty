package server;
import java.util.concurrent.*;
import java.util.*;
import java.lang.*;

public class ChatRoom{
    private Server server;
    private CopyOnWriteArrayList<Integer> clients;
    private String name;
    private String password;
    private int creator;
    private ConcurrentLinkedDeque<Message> pendingMessages;
    public ChatRoom(Server server, String identifier, String password){
        this.clients = new CopyOnWriteArrayList<Integer>();
        this.server = server;
        if(identifier != null){
            this.name = identifier;
        }
        if(password == null){
            this.password = "";
        }else{
            this.password = password;
        }
        pendingMessages = new ConcurrentLinkedDeque<Message>();
    }
    public void addClient(int i){
        if(!clients.contains(i)){
            clients.add(i);
        }
    }
    public void removeClient(Integer i){
        clients.remove(i);
    }
    public CopyOnWriteArrayList<Integer> getClients(){
        return clients;
    }
    public String getName(){
        return name;
    }
    public boolean comparePassword(String s){
        return s.equals(password);
    }
    public boolean isClientInRoom(int i){
        return clients.contains(i);
    }
    public void addMessage(Message message){
        pendingMessages.addFirst(message);
    }
    public ConcurrentLinkedDeque<Message> getMessages(){
        return pendingMessages;
    }
    public int getAmountOfMessages(){
        return pendingMessages.size();
    }

}