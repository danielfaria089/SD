package server;

import java.net.Socket;

public class ServerConnection implements Runnable{
    public static final int PORT=5678;

    private Socket socket;

    public ServerConnection(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        
    }
}
