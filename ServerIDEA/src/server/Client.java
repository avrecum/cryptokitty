package server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class Client{
    private Socket clientSocket;
    private BufferedReader clientBufferedReader;
    private BufferedWriter clientBufferedWriter;
    private CopyOnWriteArrayList<String> chatRooms;
    private DH dh;
    public Client(Socket s, DH dh){
        this.clientSocket = s;
        this.dh = dh;
        try{
        this.clientBufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.clientBufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        chatRooms = new CopyOnWriteArrayList<String>();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public Socket getSocket(){
        return this.clientSocket;
    }
    public BufferedWriter getBufferedWriter(){
        return this.clientBufferedWriter;
    }
    public BufferedReader getBufferedReader(){
        return this.clientBufferedReader;
    }
    public String encrypt(String s){
        return this.dh.encrypt(s);
    }
    public String decrypt (String s){
        return this.dh.decrypt(s);
    }
    public int sendLine(String s){
        try{
            clientBufferedWriter.write(s);
            clientBufferedWriter.newLine();
            clientBufferedWriter.flush();
        }catch(IOException e){
            return 1;
        }
        return 0;
    }
}