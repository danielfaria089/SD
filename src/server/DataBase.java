package server;

import common.Flight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {

    private Map<String, Flight> flights;
    private Map<String, Client> clients;

    public DataBase(){
        this.flights = new HashMap<>();
        this.clients = new HashMap<>();
    }

    public void addFligth(String i, String o, String d, int c, int occupation){
        Flight f = new Flight(i,o,d,c,occupation);
        flights.put(i,f);
    }

    public void addClient(String u, String p){
        Client c = new Client(u, p);
        clients.put(u,c);
    }

    public List<Flight> getSameOrigin(String origin){
        List<Flight> ret = new ArrayList<>();
        for(Flight f : flights.values()){
            if(f.getOrigin().equals(origin)) ret.add(f);
        }
        return ret;
    }

    public boolean checkDate(Flight f1, Flight f2){
        return f1.getDate().equals(f2.getDate());
    }

    public
}
