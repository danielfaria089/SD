package client.Model;

import common.Account;
import common.Frame;
import server.ServerConnection;

public class AccountManager {

    private String idLogged;
    private char[] password;

    public AccountManager(String id, char[] password){
        this.idLogged = id;
        this.password = password;
    }

    public void changePassword(char[] newPassword){
        password = newPassword.clone();
    }

    public void logOut(){
        idLogged = null;
        password = null;
    }

}
