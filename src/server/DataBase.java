package server;

import common.Flight;

import java.time.LocalDate;
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

    public void addFligth(String o, String d, int c, int occupation){
        String random = randomString();
        Flight f = new Flight(random,o,d,c,occupation);
        flights.put(random,f);
    }

    public void removeFligth(String id){
        flights.remove(id);
        for(Client c : clients.values()){
            c.removeFlight(id);
        }
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

    public void checkStopOver(String id){
        if(getStopOverOrigin(id).equals(getStopOverDestination(id))) stopOver.remove(id);
        if(stopOver.get(id).size()>3) stopOver.remove(id);
    }

    public String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 7;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void endReservesForToday(){
        for(Flight f: flights.values()){
            if(f.getDate().equals(LocalDate.now()))
                f.setOccupation(f.getCapacity());
        }

        for(Client c : clients.values()){
            c.removeFlightsByDay(LocalDate.now());
        }

        for(Map.Entry<String, List<Flight>> map : stopOver.entrySet()){
            List<Flight> aux = map.getValue();
            if(aux.get(0).getDate().equals(LocalDate.now())) stopOver.remove(map.getKey());
        }
    }

    public Map<String, List<Flight>> getFlightsWithOrigin(String origin){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if(f.getOrigin().equals(origin)) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if(f.getOrigin().equals(origin)) ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }

    public Map<String, List<Flight>> getFlightsWithDestination(String destination){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if(f.getDestination().equals(destination)) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if(f.getDestination().equals(destination)) ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }

    public Map<String, List<Flight>> getFlightsWithOriginAndDestination(String origin, String destination){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if((f.getDestination().equals(destination))&&(f.getOrigin().equals(origin))) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if((f.getDestination().equals(destination))&&(f.getOrigin().equals(origin)))
                    ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }
}
