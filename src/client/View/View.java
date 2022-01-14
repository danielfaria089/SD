package client.View;

import client.Controller.Controller;
import client.Model.ClientConnection;
import client.View.GUI.AdminWindow;
import client.View.GUI.ClientWindow;
import client.View.GUI.LoginWindow;
import client.View.GUI.Window;
import common.Helpers;

import javax.swing.text.DateFormatter;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class View {

    public static void main(String[] args){
        try{
            ClientConnection connection=new ClientConnection(Helpers.IP,Helpers.PORT);
            Controller controller=new Controller(connection);
            Window window=new LoginWindow(controller);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*

*/