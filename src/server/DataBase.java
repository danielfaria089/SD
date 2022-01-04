package server;

import common.Account;
import common.Exceptions.FlightException;
import common.Exceptions.FlightFull;
import common.Exceptions.FlightNotFound;
import common.Flight;
import common.Trip;

import java.time.LocalDate;
import java.util.*;

public class DataBase {


    private Set<Flight> defaultFlights;
    private final Map<String, Set<String>> adjencencies;

    private Map<LocalDate,Flights> bookings;
    private Map<String, Account> accounts;

    //Construtor da base de dados
    public DataBase(){
        defaultFlights=null;
        adjencencies=null;
        //adjencencies=lerFicheiroDeAdjacencias
        bookings=new HashMap<>();
        accounts=new HashMap<>();
    }

    //Adicionar todos os voos de uma trip
    public void addTrip(String id, Trip trip,LocalDate date) throws FlightFull, FlightNotFound {
        if(!bookings.containsKey(date))bookings.put(date,new Flights(defaultFlights,adjencencies));
        Flights flights=bookings.get(date);
        flights.addTrip(trip,id);
    }

    //Adicionar voo aos voos padrão
    public void addDefaultFlight(Flight flight) throws FlightException {
        for(Flight flight2:defaultFlights){
            if(flight.compareFlight(flight2)==0)throw new FlightException("Already exists");
        }
        defaultFlights.add(flight);
        for(Flights flights:bookings.values()){
            flights.addDefaultFlight(flight);
        }
    }

}

/*
    public DataBase(){
        this.flights = new HashMap<>();
        this.accounts = new HashMap<>();
        this.stopOver = new HashMap<>();
    }

    public void addFligth(String o, String d, int c){
        String random = Helpers.randomString();
        Flight f = new Flight(random,o,d,c);
        flights.put(random,f);
    }

    public void removeFligth(String id){
        flights.remove(id);
        for(Account c : accounts.values()){
            c.removeFlight(id);
        }
    }

    public void addClient(String u, char[] p){
        Account c = new Account(u, p);
        accounts.put(u,c);
    }

    public boolean checkLogIn(String id, char[] pass){
        return Arrays.equals(accounts.get(id).getPassword(), pass);
    }

    public List<Flight> getSameOrigin(String origin){
        List<Flight> ret = new ArrayList<>();
        for(Flight f : flights.values()){
            if(f.getOrigin().equals(origin)) ret.add(f);
        }
        return ret;
    }

    public void addStopOver(Flight f){
        List<Flight> aux = new ArrayList<>();
        for(Flight flight : flights.values()){
            stopOver.put(Helpers.randomString(), getSameOrigin(f.getDestination()));
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

    public void dayClosure(){
        for(Flight f : flights.values()){
            f.setOccupation(f.getCapacity());
            for(Account acc : accounts.values()){
                acc.removeFlight(f.getId());
                f.removeClient(acc);
            }
        }
    }
 */
