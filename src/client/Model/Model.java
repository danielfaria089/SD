package client.Model;

import java.io.IOException;

public class Model {

    private ClientConnection connection;

    public Model(ClientConnection connection){
        this.connection=connection;
    }

    public int login(String username,char[]password) throws IOException {
        return connection.login(username,password);
    }
}
