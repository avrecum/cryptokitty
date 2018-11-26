import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;

public class InputListener implements Runnable{
    private BufferedReader connectionBufferedReader;
    private Client connectionClient;
    private LocalCommandHandler commandHandler;
    public InputListener(BufferedReader connectionBufferedReader, Client c){
        this.connectionBufferedReader = connectionBufferedReader;
        this.connectionClient = c;
    }
    @Override
    public void run(){
        commandHandler = new LocalCommandHandler();
        String input = "";
            try{
                while(this.connectionClient.isConnected()){
                if(connectionBufferedReader.ready()){
                    input = connectionClient.decrypt(connectionBufferedReader.readLine());
                    if(!commandHandler.isServerCommand(input) &&!input.equals("___I___PING")){
                        System.out.println(input);
                    }
                }
                Thread.sleep(2);
            }
            }
            catch(IOException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return;
            }
            catch(InterruptedException i){
                i.printStackTrace();
            }
            
        
    }
}