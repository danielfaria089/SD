package client.Controller;

import client.Model.ClientConnection;
import common.Exceptions.WrongFrameTypeException;
import java.io.IOException;

public class Controller {

    private ClientConnection connection;

    public Controller(ClientConnection connection){
        this.connection=connection;
    }

    public String[] login(String username,char[]password) throws IOException, WrongFrameTypeException {
        return connection.login(username,password);
    }
    

    public String[] getCityNames() throws IOException {
        //return connection.getCities().toArray(new String[0]);
        return connection.getCities().toArray(new String[0]);
    }



}
