package server;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;

public class Server{
    private ServerSocket serverSocket;
    private InetSocketAddress bindAddress;
    private ConcurrentHashMap<String, ChatRoom> chatRooms;
    private ConcurrentHashMap<Integer, Client> clients;
    private ConcurrentLinkedDeque<Task> tasks;
    private ServerListener serverListener;
    private ServerHeartbeater serverHeartbeater;
    private MessageHandler messageHandler;
    private MessageSender messageSender;
    private CommandExecutor commandExecutor;
    private Thread serverListenerThread;
    private Thread serverHeartbeaterThread;
    private Thread messageHandlerThread;
    private Thread messageSenderThread;
    private Thread commandExecutorThread;
    public Server(){
        try{
            serverSocket = new ServerSocket();
        }catch(Exception e){
            //TODO: Handle Exceptions
            e.printStackTrace();
        }
        
    }
    public Server(int port){
        try{
            serverSocket = new ServerSocket();
        }catch(Exception e){
            //TODO: Handle exceptions
            e.printStackTrace();
        }
        
        this.setConnectionAddress(null, port);
    }
    public Server(String bindAddress, int port){
        try{
            serverSocket = new ServerSocket();
        }catch(Exception e){
            //TODO: Handle Exceptions
            e.printStackTrace();
        }
        
        this.setConnectionAddress(bindAddress, port);
    }
    public void setConnectionAddress(String bindAddress, int port){
        if(bindAddress != null){
            try{
                this.bindAddress = new InetSocketAddress(bindAddress, port);
            }catch(IllegalArgumentException e){
                System.out.println("Invalid bind address or port specified: " + this.bindAddress.toString());
            }

        }
        if(bindAddress == null){
            try{
                this.bindAddress = new InetSocketAddress("0.0.0.0", port);
            }catch(IllegalArgumentException e){
                System.out.println("Invalid port specified: " + this.bindAddress.getPort());
            }
        }
    }
    public boolean hasBindAddress() {
        return this.bindAddress != null;
    }

    public void bind(){
        if(this.bindAddress == null || this.serverSocket == null){
            System.out.println("Could not bind: no bind address (optional) or port specified.");
        }else{
            try{
                serverSocket.bind(bindAddress);
            }catch(IOException e){
                System.out.println("Could not bind to" + bindAddress.getHostString()!= null? bindAddress.getHostString(): "" + "port "+ this.bindAddress.getPort()+". "+e.getMessage());
            }
            if(!this.serverSocket.isBound()){
                System.out.println("Something went wrong while binding. Is the port already in use?");
            }else{
                System.out.println("Successfully bound to " + bindAddress.getHostString() + " port "+this.bindAddress.getPort()+ ". Starting Listener.");
                this.chatRooms = new ConcurrentHashMap<String, ChatRoom>();
                this.clients = new ConcurrentHashMap<Integer, Client>();
                this.serverListener = new ServerListener(this.serverSocket, this);
                this.serverListenerThread = new Thread(this.serverListener);
                this.serverListenerThread.setName("Server Listener Thread");
                this.serverListenerThread.start();
                this.serverHeartbeater = new ServerHeartbeater(clients, this);
                this.serverHeartbeaterThread = new Thread(this.serverHeartbeater);
                this.serverHeartbeaterThread.setName("Server Heartbeater Thread");
                this.serverHeartbeaterThread.start();
                this.messageHandler = new MessageHandler(clients, this);
                this.messageHandlerThread = new Thread(this.messageHandler);
                this.messageHandlerThread.setName("Message Handler Thread");
                this.messageHandlerThread.start();
                this.messageSender = new MessageSender(this, this.chatRooms);
                this.messageSenderThread = new Thread(this.messageSender);
                this.messageSenderThread.setName("Message Sender Thread");
                this.messageSenderThread.start();
                this.tasks = new ConcurrentLinkedDeque<Task>();
                this.commandExecutor = new CommandExecutor(this, tasks);
                this.commandExecutorThread = new Thread(this.commandExecutor);
                this.commandExecutorThread.setName("Command Executor Thread");
                this.commandExecutorThread.start();
            }
        }
    }
    public boolean isBound(){
        return this.serverSocket.isBound();
    }
    public int addClientToRoom(Client c, String roomIdentifier, String password){
        ChatRoom chatRoom = this.chatRooms.get(roomIdentifier);
        if(chatRoom == null){
            return 2;
        }else{
            if(chatRoom.comparePassword(password)){
                chatRoom.addClient(c.hashCode());
                return 0;
            }
            else {
                return 1;
            }
        }
        }

    public void addClient(Client client){
        this.clients.put(client.hashCode(), client);
    }
    public void removeClient(Integer hashCode){
        this.clients.remove(hashCode);
    }
    public void createRoom(String identifier, String password){
        this.chatRooms.put(identifier, new ChatRoom(this, identifier, password));
    }
    public ChatRoom getRoom(String identifier){
        return chatRooms.get(identifier);
    }
    public void addTask(Task t){
        tasks.addFirst(t);
    }
    public ConcurrentLinkedDeque<Task> getTasks(){
        return this.tasks;
    }
    public ConcurrentHashMap<Integer, Client> getClients(){
        return this.clients;
    }
}