import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
public class OutputListener implements Runnable{
    private BufferedWriter connectionBufferedWriter;
    private Client connectionClient;
    private LocalCommandHandler commandHandler;
    private Scanner sc;
    private String output;
    public OutputListener(BufferedWriter connectionBufferedWriter, Client c){
        this.connectionBufferedWriter = connectionBufferedWriter;
        this.connectionClient = c;
    }
    @Override
    public void run(){
        sc = new Scanner(System.in);
        commandHandler = new LocalCommandHandler();
            try{
                while(this.connectionClient.isConnected()){
                    if(sc.hasNextLine()){
                    output = sc.nextLine();
                    if(!commandHandler.isLocalCommand(output)){
                        if(!Thread.currentThread().isInterrupted()){
                            connectionBufferedWriter.write(connectionClient.encrypt(output));
                            connectionBufferedWriter.newLine();
                            connectionBufferedWriter.flush();   
                        }
                    }else{
                        commandHandler.handle(output);
                    }
                }
            }       
            }
            catch(IOException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
                sc.close();
                return;
            }
            sc.close();
        }
        }
        
    
