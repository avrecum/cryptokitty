/*
Creates a client Object.
API:
Client()
    Creates an empty client object that can later be connected to a Server through the connect(address, port) method.
Client(address, port)
    Takes the target address as a String and Port as an int. Attempts to connect to address:port, otherwise a ConnectException is thrown.
connect(address, port)
    Attempts to establish a connection to a server at address:port. If a connection is already active, an exception is thrown.
closeConnection()
    Closes the TCP connection to the Server. If called with no open connection, an exception is thrown.

*/
import java.io.*;
import java.net.*;
import java.lang.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class Client{
    private boolean connected;
    private Socket connectionSocket;
    private InetSocketAddress connectionAddress;
    private int timeout = 0;
    private OutputStream connectionOutputStream;
    private InputStream connectionInputStream;
    private OutputStreamWriter connectionOutputStreamWriter;
    private InputStreamReader connectionInputStreamReader;
    private BufferedWriter connectionBufferedWriter;
    private BufferedReader connectionBufferedReader;
    private ClientHeartbeater connectionHeartbeater;
    private Thread connectionHeartbeaterThread;
    private OutputListener connectionOutputListener;
    private InputListener connectionInputListener;
    private Thread connectionOutputListenerThread;
    private Thread connectionInputListenerThread;
    private DH dh;
    public Client() {
        connectionSocket = new Socket();
    }
    public Client(String address, int port) {
        connectionSocket = new Socket();
        this.setConnectionAddress(address, port);
    }
    public Client(String address, int port, int connectionTimeout) throws IllegalArgumentException{
        connectionSocket = new Socket();
        this.setConnectionAddress(address, port);
        if(connectionTimeout < 0) {
            throw new IllegalArgumentException("Timeout must be greater than or equal to 0.");
        }
        else {
            this.timeout = connectionTimeout;
        }
    }
    public void setConnectionAddress(String address, int port) {
        try {
            connectionAddress = new InetSocketAddress(address, port);
        }
        catch(IllegalArgumentException e) {
            System.out.println("Invalid port specified: " + port);
        }
    }
    public void setConnectionTimeout(int timeout) {
        this.timeout = timeout;
    }
    public boolean hasAddressAndPort() {
        return connectionAddress != null;
    }
    public void connect(){
        if(!this.hasAddressAndPort()){
            System.out.println("Could not connect: no address and/or port specified.");
        }else{
            boolean connectionFailed = true;
            try{
                connectionSocket.connect(connectionAddress, this.timeout);
            }catch(SocketTimeoutException f){
                System.out.println("Connection timeout. Could not connect after "+ timeout + " ms.");
                connectionFailed = true;
            }catch(IOException e){
                System.out.println("Connection to " + connectionAddress.getHostString()+":"+connectionAddress.getPort()+" failed. ERROR: "+ e.getMessage());
                connectionFailed = true;
            }
            if(!connectionFailed) {
                System.out.println("Connected to: " + connectionSocket.getRemoteSocketAddress() + connectionSocket.getPort());
                try {
                    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
                    BufferedReader r = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    System.out.println("Attempting key exchange with server.");
                    String encodedServerPubKey = r.readLine();
                    Base64.Decoder dec = Base64.getDecoder();
                    Base64.Encoder enc = Base64.getEncoder();
                    byte[] decodedByteArray = dec.decode(encodedServerPubKey);
                    this.dh = new DH();
                    PublicKey pk = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(decodedByteArray));
                    dh.setReceiverPublicKey(pk);
                    System.out.println("Received server public key. Sending client public key to server.");
                    String encodedClientPubKey = enc.encodeToString(dh.getPublickey().getEncoded());
                    w.write(encodedClientPubKey);
                    w.newLine();
                    w.flush();
                    String check = r.readLine();
                    String decrypted = dh.decrypt(check);
                    if (decrypted.equals("success")) {
                        System.out.println("Key exchange successful.");
                        this.handleConnection();
                    }else{
                        System.out.println("Key exchange failed.");
                    }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            else {
                System.out.println("Connection failed.");
            }
            
        }
    }
    public boolean isConnected(){
        return this.connected;
    }
    public void handleConnection() {
        try{
            this.connected = true;
            this.connectionOutputStream = connectionSocket.getOutputStream();
            this.connectionInputStream = connectionSocket.getInputStream();
            this.connectionOutputStreamWriter = new OutputStreamWriter(this.connectionOutputStream);
            this.connectionInputStreamReader = new InputStreamReader(this.connectionInputStream);
            this.connectionBufferedWriter = new BufferedWriter(this.connectionOutputStreamWriter);
            this.connectionBufferedReader = new BufferedReader(this.connectionInputStreamReader);
            this.connectionHeartbeater = new ClientHeartbeater(this.connectionOutputStream, this.connectionSocket, this);
            this.connectionHeartbeaterThread = new Thread(this.connectionHeartbeater);
            this.connectionHeartbeaterThread.start();
            this.connectionInputListener = new InputListener(this.connectionBufferedReader, this);
            this.connectionInputListenerThread = new Thread(this.connectionInputListener);
            this.connectionOutputListenerThread = new Thread(this.connectionOutputListener);
            this.connectionInputListenerThread.start();
            this.connectionOutputListenerThread.start();
            
        }catch(IOException e){
            System.out.println("Failed to handle connection: " +e.getMessage());
            e.printStackTrace();
        }
    }
    public void closeConnection(){
        try{
            this.connected = false;
            this.connectionSocket.close();
            this.connectionBufferedWriter.close();
            this.connectionBufferedReader.close();
            this.connectionHeartbeaterThread.interrupt();
            this.connectionInputListenerThread.interrupt();
            this.connectionOutputListenerThread.interrupt();
            System.exit(0);
        }catch(IOException e){
            System.out.print("Failed to close connection: " +e.getMessage());
            e.printStackTrace();
        }
    }
    public String decrypt(String s){
        return dh.decrypt(s);
    }
    public String encrypt(String s){
        return dh.encrypt(s);
    }
    public BufferedWriter getClientBufferedWriter(){
        return connectionBufferedWriter;
    }
    
}