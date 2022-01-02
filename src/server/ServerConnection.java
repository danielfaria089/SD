package server;

import common.Frame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection implements Runnable{
    public static final int PORT=5678;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Accounts accounts;

    public ServerConnection(Socket socket,Accounts accounts) throws IOException {
        this.socket=socket;
        this.accounts=accounts;
        input=new DataInputStream(socket.getInputStream());
        output=new DataOutputStream(socket.getOutputStream());
    }

    public boolean receive() throws IOException {
        Frame frame=new Frame();
        frame.deserializeDIS(input);
        switch (frame.getType()){
            case 0:
                return true;
            case 1:
                password(frame);
                return true;
            default:
                return false;
        }
    }

    private void password(Frame frame) throws IOException {
        
    }

    @Override
    public void run(){
        while(true) {
            try {
                if (!receive()) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
