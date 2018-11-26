package server;

import java.util.*;
import java.util.concurrent.*;
import java.lang.Thread;
import java.io.*;

public class ServerHeartbeater implements Runnable{
    private ConcurrentHashMap<Integer, Client> clients;
    private Server server;
    public ServerHeartbeater(ConcurrentHashMap<Integer, Client> clients, Server s){
        this.clients = clients;
        this.server = s;
    }
    @Override
    public void run(){
        Client currentClient = null;
        BufferedWriter currentWriter;
        while(this.server.isBound()){
            Iterator<Map.Entry<Integer, Client>> entries = clients.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<Integer, Client> entry = entries.next();
                currentClient = entry.getValue();
                int status = currentClient.sendLine(currentClient.encrypt("___I___PING"));
                if(status == 1){
                    System.out.println("Client disconnected: " + currentClient.getSocket().getInetAddress().getHostAddress()+":"+ currentClient.getSocket().getPort());
                    try{ currentClient.getSocket().close();
                    }catch(IOException f){
                        f.printStackTrace();
                    }
                    entries.remove();
                }
            }
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            }
        return;
    }
    
}