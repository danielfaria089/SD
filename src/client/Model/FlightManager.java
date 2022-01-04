package client.Model;

import common.Account;
import common.Exceptions.FlightFull;
import common.Flight;

public class FlightManager {

    private Account acc;

    public FlightManager(String id, char[] pass){
        acc = new Account(id,pass);
    }

    public void flightReservation(Flight f) throws FlightFull {
        acc.addFlight(f);
        f.addPassenger(acc);
    }

    public void cancelFlight(Flight f){
        acc.removeFlight(f.getId());
        f.removeClient(acc);
    }


}
