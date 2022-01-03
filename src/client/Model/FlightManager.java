package client.Model;

import common.Account;
import common.Flight;

public class FlightManager {

    private Account acc;

    public FlightManager(String id, char[] pass){
        acc = new Account(id,pass);
    }

    public void flightReservation(Flight f){
        acc.addFlight(f);
        f.addClient(acc);
    }

    public void cancelFlight(Flight f){
        acc.removeFlight(f.getId());
        f.removeClient(acc);
    }


}
