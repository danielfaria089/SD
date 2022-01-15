package client.View;

import client.Controller.Controller;
import client.Model.ClientConnection;
import client.View.GUI.LoginWindow;
import client.View.GUI.Window;
import common.Helpers;

import java.io.IOException;

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
        try{
            ClientConnection connection=new ClientConnection(Helpers.IP,Helpers.PORT);
            Controller controller=new Controller(connection);
            controller.login("admin",new char[]{'1','2','3','4'});
            controller.adicionaDefaultFlight("Teste","Testa","1");

            controller.login("blanc",new char[]{'b','l','a','n','c'});
            List<String> stops=new ArrayList<>();
            stops.add("Lisboa");
            stops.add("Kiev");
            stops.add("HongKong");
            String res=controller.specificReservation(stops, LocalDate.of(2022,2,15),LocalDate.of(2022,2,15));
            controller.cancelBooking(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/