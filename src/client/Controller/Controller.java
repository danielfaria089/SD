package client.Controller;

import client.Model.Model;

import java.io.IOException;

public class Controller {

    private Model model;

    public Controller(Model model){
        this.model=model;
    }

    public boolean login(String username,char[]password) throws IOException {
        return model.login(username,password);
    }




}
