import java.lang.Thread;
import java.net.Socket;
import java.io.*;
public class ClientHeartbeater implements Runnable{
    private OutputStream out;
    private Socket s;
    private Client c;
    public ClientHeartbeater(OutputStream out, Socket s, Client c){
        this.out = out;
        this.s = s;
        this.c = c;
    }
    @Override
    public void run(){
        try{
            Thread.currentThread().setName("ClientHeartbeater");
            while(this.s.isConnected()){
                Thread.sleep(10000);
                BufferedWriter w = c.getClientBufferedWriter();
                w.write(c.encrypt("!!PING"));
                w.newLine();
                w.flush();
            }
        }catch(InterruptedException e){
            System.out.println("ClientHeartbeater interrupted!");
            Thread.currentThread().interrupt();
            return;
        }catch(IOException e){
            System.out.println("Connection to server lost.");
            try{
                c.closeConnection();
             }catch(Exception f){
                 f.printStackTrace();
             }
             return;
        }
    }
}