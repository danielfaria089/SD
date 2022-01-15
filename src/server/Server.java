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

        List<Flight> flight = new ArrayList<>();
        Set<Flight> flights = new HashSet<>();
        flights = db.getDefaultFlights();
        for(Flight fax : flights){
            if(fax.getOrigin().equals("Oporto")&& fax.getDestination().equals("Lisbon")){
                flight.add(fax);
                break;
            }
        }

        Booking b = new Booking("jahbdha", "tortuga", LocalDate.of(2022,9,11), flight);

        db.addBooking(b);
        db.writeBookings(BOOKING_FILE);
        db.writeAccounts(ACCOUNTS_FILE);
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
