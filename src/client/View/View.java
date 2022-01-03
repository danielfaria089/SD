package client.View;

import client.Controller.Controller;
import client.Model.ClientConnection;
import client.Model.Model;
import client.View.GUI.LoginWindow;
import common.Helpers;

import java.io.IOException;

public class View {

    public static void main(String[] args){
        try{
            ClientConnection connection=new ClientConnection(Helpers.IP, Helpers.PORT);
            Model model=new Model(connection);
            Controller controller=new Controller(model);
            LoginWindow teste=new LoginWindow(controller);
            teste.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
