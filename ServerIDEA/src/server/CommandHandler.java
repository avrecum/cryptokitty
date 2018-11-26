package server;

import commands.Join;
import commands.Leave;

public class CommandHandler {
    private Server server;
    public CommandHandler(Server server) {
        this.server = server;
    }

    public int isCommand(String s){
        if(s.length()>2) {
            if (s.charAt(0) == '!' && s.charAt(1) == '!') {
                return 1;
            }
            int startIndex = getStartIndex(s);
            String a = s.substring(startIndex);
            if (startIndex+2 <= a.length()) {
                if (a.charAt(startIndex) == '!' && a.charAt(startIndex + 1) == '!') {
                    return 2;
                }
            }
        }
        return 0;
    }
    public void handleGlobalCommand(Client client, String s){
        String command = recognizeCommand(s);
        if(!command.equals("invalid")) {
            String message = Message.isolateMessage(s, command);
            server.addTask(new Task(client, command, message));
        }
    }

    public void handleLocalCommand(Client client, String s){
        int startIndex = getStartIndex(s);
        String sub = s.substring(startIndex);
        String command = recognizeCommand(sub);
        if(!command.equals("invalid")) {
            String message = Message.isolateMessage(s, command);
            server.addTask(new Task(client, command, message));
        }
    }
    private static int getStartIndex(String s){
        int startIndex = 0;
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i)==':') startIndex = i+1;
        }
        return startIndex;
    }
    private static String recognizeCommand(String s){
        s = s.toLowerCase();
        if(s.contains("join")){

            return "join";
        }
        if(s.contains("leave")){

            return "leave";
        }
        if(s.contains("createroom")){
            return "createroom";
        }
        if(s.contains("setname")){
            return "setname";
        }

        return "invalid";
    }
    //take apart the command. remove 2 first chars,

}
