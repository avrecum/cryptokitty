public class RunClient{
    public static void main(String[] args){
        Client c = new Client();
        c.setConnectionAddress("localhost", 8888);
        c.setConnectionTimeout(10000);
        c.connect();
    }
}
