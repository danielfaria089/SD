package client.Model;

import common.Account;
import common.Exceptions.FlightFull;
import common.Flight;

import java.time.LocalDate;

public class FlightManager {

    private Account acc;

    public FlightManager(String id, char[] pass){
        acc = new Account(id,pass);
    }

    public void flightReservation(Flight f, LocalDate date) throws FlightFull {
        acc.addFlight(f,date);
        f.addPassenger(acc);
    }

    public void cancelFlight(Flight f){
        acc.removeFlight(f.getId());
        f.removeClient(acc);
    }


}
