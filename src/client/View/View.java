package client.View;

import client.Controller.Controller;
import client.Model.ClientConnection;
import client.View.GUI.ClientWindow;
import client.View.GUI.Window;
import common.Helpers;

import java.awt.*;
import java.io.IOException;

public class View {

    public static void main(String[] args){
        try{
            ClientConnection connection=new ClientConnection(Helpers.IP,Helpers.PORT);
            Controller controller=new Controller(connection);
            Window window=new ClientWindow(controller,new Dimension(600,400));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

/*

*/



/*
    try{
            ClientConnection connection=new ClientConnection(Helpers.IP, Helpers.PORT);
            Model model=new Model();
            Controller controller=new Controller(model);
            Window teste=new ClientWindow(controller,new Dimension(600,400));
            teste.show();
        }catch (IOException e) {
            e.printStackTrace();
        }



*/