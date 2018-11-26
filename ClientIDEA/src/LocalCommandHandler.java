public class LocalCommandHandler{
    public LocalCommandHandler(){

    }
    public void handle(String command){

    }
    public boolean isLocalCommand(String s){
        if(s.length()>1) {
            return s.charAt(0) == '!' && s.charAt(1) == '?';
        }
        return false;
    }
    public boolean isServerCommand(String s){
        if(s.length()>1) {
            return s.charAt(0) == '!' && s.charAt(1) == '!';
        }
        return false;
    }
}