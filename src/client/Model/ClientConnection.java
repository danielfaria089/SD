package client.Model;

import common.Credentials;
import common.Frame;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientConnection {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientConnection(String ip,int port) throws IOException {
        clientSocket=new Socket(ip,port);
        input=new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        output=new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
    }

    public int login(String username,char[]password) throws IOException {
        Credentials credentials=new Credentials(username,password);
        output.write(credentials.createFrame().serialize());
        output.flush();
        Frame response=new Frame();
        response.deserialize(input);
        if(response.getType()==0){
            List<byte[]> dados=response.getData();
            String resposta=new String(dados.get(0), StandardCharsets.UTF_8);
            if(resposta.equals("CLIENT"))return 1;
            else if(response.equals("ADMIN"))return 2;
        }
        else return -1;
    }
}
