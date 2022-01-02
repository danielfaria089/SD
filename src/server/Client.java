package server;

import common.Flight;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private String username;
    private String password;
    private Map<String, Flight> flights;

    public Client(String u, String p){
        this.username = u;
        this.password = p;
        this.flights = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addFlight(Flight f){
        flights.put(f.getId(), f);
    }

    public void removeFlight(Flight f){
        flights.remove(f.getId());
    }


}
