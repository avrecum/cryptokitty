import java.net.*;
import java.lang.*;
import java.util.*;
public class test {
    public static void main(String[] args){
        System.out.println(args.hashCode());
        InetSocketAddress s = new InetSocketAddress("localhost",4444);
        System.out.println(s.toString());
    }
}
