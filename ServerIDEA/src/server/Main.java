package server;

import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.util.Base64.Encoder;

public class Main{
    public static void main(String[] args){
        Server s = new Server(8888);
        s.bind();
        s.createRoom("w", "");
        s.createRoom("c", "c");
    }
}