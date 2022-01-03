package server;

import common.Flight;

import java.util.*;

public class DataBase {

    private Map<String, Flight> flights;
    private Map<String, Client> clients;
    private Map<String, List<Flight>> stopOver;

    public DataBase(){
        this.flights = new HashMap<>();
        this.clients = new HashMap<>();
        this.stopOver = new HashMap<>();
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

    public void addStopOver(Flight f){
        List<Flight> aux = new ArrayList<>();
        for(Flight flight : flights.values()){
            if(checkDate(f,flight)) stopOver.put(randomString(), getSameOrigin(f.getDestination()));
        }
    }

    public String getStopOverOrigin(String id){
        return stopOver.get(id).get(0).getOrigin();
    }

    public String getStopOverDestination(String id){
        int length = stopOver.get(id).size();
        return stopOver.get(id).get(length-1).getDestination();
    }

    public String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
