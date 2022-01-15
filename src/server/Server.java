package server;

import common.Account;
import common.Booking;
import common.Flight;
import common.Helpers;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {

    private static final String ACCOUNTS_FILE="src/server/Files/Accounts";
    private static final String FLIGHTS_FILE="src/server/Files/Flights";
    private static final String BOOKING_FILE="src/server/Files/Bookings";

    final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(Helpers.PORT);
        DataBase db = new DataBase();
        db.addClient("eu",new char[]{' '},false);

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
