package server;

import java.util.Arrays;
import java.util.Map;

public class Accounts {
    //private Map<String,Client> clients;
    private Map<String,char[]> accounts;

    public boolean login(String username,char[] password){
        if(accounts.containsKey(username)){
            char[] passUser=accounts.get(username);
            return Arrays.equals(passUser,password);

        }
        else return false;
    }
}
