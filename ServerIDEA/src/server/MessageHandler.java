package server;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.concurrent.*;
public class MessageHandler implements Runnable{
    private Server server;
    private CommandHandler commandHandler;
    private ConcurrentHashMap<Integer, Client> clients;
    private BufferedReader r;
    private BufferedWriter w;
    private String currentMessage;
    public MessageHandler(ConcurrentHashMap<Integer, Client> clients, Server server){
        this.server = server;
        this.clients = clients;
    }
    @Override
    public void run(){
        Client currentClient;
        commandHandler = new CommandHandler(this.server);
        while(this.server.isBound()){
            Iterator it = clients.entrySet().iterator();
            while (it.hasNext()) {
                try{
                    Map.Entry currentEntry = (Map.Entry) it.next();
                    currentClient = (Client) currentEntry.getValue();
                    r = currentClient.getBufferedReader();
                    while(r.ready()){
                        currentMessage = currentClient.decrypt(r.readLine());
                        System.out.println("Got message." + currentMessage);
                        int result = commandHandler.isCommand(currentMessage);
                        if(result == 2){
                            commandHandler.handleLocalCommand(currentClient, currentMessage);
                            System.out.println("got local command"+ currentMessage);
                        }else if(result == 1){
                            commandHandler.handleGlobalCommand(currentClient, currentMessage);
                            System.out.println("got global command"+ currentMessage);
                        }else{
                            System.out.println("Got message. Trying to handle.");
                            String identifier = Message.getChatroomIdentifier(currentMessage);
                            System.out.println(identifier);
                            ChatRoom chatRoom = this.server.getRoom(identifier);
                            if(chatRoom != null) {
                                if (chatRoom.isClientInRoom(currentClient.hashCode())) {
                                    System.out.println("added" + currentMessage + "to room" + chatRoom.getName());
                                    chatRoom.addMessage(new Message(currentClient, currentMessage));
                                }
                            }
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
            try{
            Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}