package server;

import common.Exceptions.AccountException;
import common.Frame;
import common.Helpers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        DataBase db = new DataBase();
        db.addClient("ola", new char[]{'1', '2', '3'});
        db.adicionarNotificacaoACliente("ola","cona cona cona cona cona");
        while(true) {
            Socket s = ss.accept();
            ServerConnection sc = new ServerConnection(s,db);


            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(sc).start();
        }

    }
}

/*
        try{
            boolean run=true;
            DataBase base=new DataBase();
            base.addClient("teste",new char[]{'c','o','n','a'});
            ServerSocket serverSocket=new ServerSocket(Helpers.PORT);

            while(run){
                Socket clientsocket=serverSocket.accept();
                new ServerConnection(clientsocket,base).run();
            }
        } catch (IOException | AccountException e) {
            e.printStackTrace();
        }
 */
