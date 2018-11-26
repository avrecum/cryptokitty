package server;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
public class ServerListener implements Runnable{
    private ServerSocket serverSocket;
    private Server server;
    public ServerListener(ServerSocket serverSocket, Server server){
        this.serverSocket = serverSocket;
        this.server = server;
    }
    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try{
                Socket s = this.serverSocket.accept();
                System.out.println("Accepted client!");
                //TODO: add Diffie-Hellman key exchange and save inside client object
                //Send public key to client, then wait for public key
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DH dh = new DH();
                Base64.Encoder enc = Base64.getEncoder();
                Base64.Decoder dec = Base64.getDecoder();
                String encodedKey = enc.encodeToString(dh.getPublickey().getEncoded());
                w.write(encodedKey);
                w.newLine();
                w.flush();
                String encodedClientPubKey = r.readLine();
                byte[] clientPubKey = dec.decode(encodedClientPubKey);
                PublicKey pk = null;
                try {
                    pk = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(clientPubKey));
                }catch(NoSuchAlgorithmException e){
                    e.printStackTrace();
                }catch (InvalidKeySpecException f){
                    System.out.println("Key Exchange with client failed.");
                }
                if(pk != null){
                    dh.setReceiverPublicKey(pk);
                    String check = dh.encrypt("success");
                    w.write(check);
                    w.newLine();
                    w.flush();
                    this.server.addClient(new Client(s, dh));
                }

            }catch(IOException e){
                System.out.println("I/O Error occurred. " + e.getMessage());
            }
        }
    }
}