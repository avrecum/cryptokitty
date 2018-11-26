package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MessageSender implements Runnable{
    private Server server;
    private ConcurrentHashMap<String, ChatRoom> chatRooms;
    private ConcurrentHashMap<Integer, Client> clients;
    private ChatRoom currentChatRoom;
    private Client currentClient;
    public MessageSender(Server server, ConcurrentHashMap<String, ChatRoom> chatRooms){
        this.server = server;
        this.chatRooms = chatRooms;
        this.clients = server.getClients();
    }

    public void run(){
        while(Thread.currentThread().isAlive()){
            for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
                currentChatRoom = entry.getValue();
                if(currentChatRoom.getAmountOfMessages() > 0){
                    ConcurrentLinkedDeque<Message> messages = currentChatRoom.getMessages();
                    CopyOnWriteArrayList<Integer> clients = currentChatRoom.getClients();
                    for(int i: clients) {
                        currentClient = this.clients.get(i);
                        if (currentClient != null) {
                            for (Message m : messages) {
                                if (!(currentClient.hashCode() == m.getClient().hashCode())) {
                                    int status = currentClient.sendLine(currentClient.encrypt(m.getClient().hashCode()+ ": " + m.getMessage()));
                                    if(status == 1){
                                        clients.remove(currentClient.hashCode());
                                    }
                                }
                            }
                        }else{
                            currentChatRoom.removeClient(i);
                        }
                    }
                    messages.clear();
                }
            }

            try {
                Thread.currentThread().sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
