package server;

import java.util.Arrays;
import java.util.Map;

public class Accounts {
    //private Map<String,Client> clients;
    private Map<String,char[]> clients;

    public boolean login(String username,char[] password){
        if(clients.containsKey(username)){
            char[] passUser=clients.get(username);
            return Arrays.equals(passUser,password);
        }
        else return false;
    }
}
