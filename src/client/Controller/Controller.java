package client.Controller;

import client.Model.ClientConnection;
import common.Exceptions.WrongFrameTypeException;
import common.Flight;
import common.StopOvers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private ClientConnection connection;

    public Controller(ClientConnection connection){
        this.connection=connection;
    }

    public String[] login(String username,char[]password) throws IOException, WrongFrameTypeException {
        return connection.login(username,password);
    }
    
    /*
    public String[] getCityNames(){
        return connection.getCities().toArray(new String[0]);
    }

    */

}
