package server;

import common.Credentials;
import common.Frame;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection implements Runnable{
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private DataBase dataBase;

    public ServerConnection(Socket socket,DataBase dataBase) throws IOException {
        this.socket=socket;
        this.dataBase=dataBase;
        input=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public boolean receive() throws IOException {
        Frame frame=new Frame();
        frame.deserialize(input);
        switch (frame.getType()){
            case (byte)1:
                login(frame);
                break;
            case (byte)2:
                reservation(frame);
                break;
        }
        return true;
    }

    private void login(Frame frame) throws IOException {
        Credentials credentials=new Credentials(frame);
        if(dataBase.checkLogIn(credentials.getUsername(), credentials.getPassword())){
            Frame success=new Frame((byte)0);
            success.addBlock("SUCCESS".getBytes(StandardCharsets.UTF_8));
            output.write(success.serialize());
            output.flush();
        }
        else{
            Frame failure=new Frame((byte)0);
            failure.addBlock("ERROR".getBytes(StandardCharsets.UTF_8));
            output.write(failure.serialize());
            output.flush();
        }
    }

    private void reservation(Frame frame){

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
