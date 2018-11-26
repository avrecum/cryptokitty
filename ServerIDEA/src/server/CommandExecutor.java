package server;
import commands.*;
import java.util.*;
import java.util.concurrent.*;

public class CommandExecutor implements Runnable{
    private Server server;
    private ConcurrentLinkedDeque<Task> tasks;
    private Join join;
    private Leave leave;
    private CreateRoom createroom;
    public CommandExecutor(Server server, ConcurrentLinkedDeque<Task> tasks){
        this.server = server;
        this.tasks = tasks;
        this.join = new Join(this.server);
        this.leave = new Leave(this.server);
        this.createroom = new CreateRoom(this.server);
    }

    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            if(!tasks.isEmpty()){
                try {
                    Task t = tasks.getLast();
                    if (t != null) {
                        switch (t.getType()) {
                            case "join":
                                this.join.join(t.getClient(), t.getMessage());
                                tasks.removeLast();
                                break;
                            case "leave":
                                this.leave.leave(t.getClient(), t.getMessage());
                                tasks.removeLast();
                                break;
                            case "createroom":
                                this.createroom.createroom(t.getClient(), t.getMessage());
                                tasks.removeLast();
                                break;
                            default:
                        }
                    }
                }catch(NoSuchElementException e){
                    e.printStackTrace();
                }
            }else{
                try {
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
