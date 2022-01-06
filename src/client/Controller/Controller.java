package client.Controller;

import client.Model.Model;
import common.Exceptions.WrongFrameTypeException;

import java.io.IOException;

public class Controller {

    private Model model;

    public Controller(Model model){
        this.model=model;
    }

    public int login(String username,char[]password) throws IOException, WrongFrameTypeException {
        return model.login(username,password);
    }




}
