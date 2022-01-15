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

        List<Flight> flights = new ArrayList<>();
        for(Flight f : db.getDefaultFlights()){
            if(f.getOrigin().equals("Oporto")&&f.getDestination().equals("Lisbon")) flights.add(f);
        }

        Booking b = new Booking("kpokdyt", "tortuga" , LocalDate.of(2022,9,12),flights);

        db.writeAccounts(ACCOUNTS_FILE);
        db.writeBookings(BOOKING_FILE);
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
