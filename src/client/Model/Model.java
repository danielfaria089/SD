package client.Model;

import common.Exceptions.WrongFrameTypeException;
import java.util.*;
import java.io.IOException;

public class Model {

    private ClientConnection connection;

    public Model(ClientConnection connection){
        this.connection=connection;
    }

    public int login(String username,char[]password) throws IOException, WrongFrameTypeException {
        return connection.login(username,password);
    }

    public Set<String> getCities(){
        //return connection.getCities();  //tirar de comentário quando estiver implementado
        return null;
    }
}
