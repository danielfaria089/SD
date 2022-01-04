package common;

import common.Flight;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Account {

    private String username;
    private char[] password;
    private Map<String, Pair<Flight,LocalDate>> flights;

    public Account(String u, char[] p){
        this.username = u;
        this.password = p.clone();
        this.flights = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public void addFlight(Flight f, LocalDate date){
        flights.put(f.getId(), new Pair<>(f,date));
    }

    public void removeFlight(String id){
        flights.remove(id);
    }
}
