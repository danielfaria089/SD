package server;

import common.Exceptions.DayClosedException;
import common.Exceptions.FlightFullException;
import common.Exceptions.FlightNotFoundException;
import common.Flight;
import common.Trip;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//Voos de um certo dia
public class Flights {
    //Número máximo de escalas
    public static final int MAX_FLIGHTS=3;

    private Set<Flight> flights; //Voos do dia
    private final Map<String, Set<String>> adjencencies; //Adjacências entre as várias cidades
    private boolean closed; //Se o dia está fechado ou não

    //Construtor com voos do dia e adjacências de cidades
    public Flights(Set<Flight> flights,Map<String, Set<String>> adjencencies){
        this.adjencencies=adjencencies;
        this.flights=flights.stream().map(Flight::clone).collect(Collectors.toSet());
        closed=false;
    }

    //Adiciona uma viagem ao dia
    public void addTrip(Trip trip,String id) throws FlightFullException, FlightNotFoundException, DayClosedException {
        if(closed)throw new DayClosedException();
        boolean found;
        for(Flight flight1:trip.getStopOvers()){
            found=false;
            for(Flight flight2:flights){
                if(flight1.compareFlight(flight2)==0) {
                    flight2.addPassenger(id);
                    found=true;
                }
            }
            if(!found)throw new FlightNotFoundException(flight1.getOrigin()+" -> "+flight1.getDestination()+" not found");
        }
    }

    //Adiciona um voo default
    public void addDefaultFlight(Flight flight){
        flights.add(flight);
    }

    //Devolve viagens possiveis entre origem e destino
    public Set<Trip> getFlights(String origin,String destination) throws IOException {
        Set<Trip> trips= new TreeSet<>(Trip::compare);
        List<List<String>> ways=new ArrayList<>();
        List<String> current=new ArrayList<>();
        current.add(origin);
        depthFirst(origin,destination,ways,current);
        for(List<String>strings:ways){
            Trip trip=new Trip();
            for(int i=0;i< strings.size()-1;i++){
                String o=strings.get(i);
                String d=strings.get(i+1);
                for(Flight flight:flights){
                    if(flight.equals(o,d)) {
                        trip.addFlight(flight);
                        break;
                    }
                }
            }
            trips.add(trip);
        }
        return trips;
    }

    //depthFirst search
    private void depthFirst(String origin,String destination,List<List<String>> added,List<String>current){
        if(current.size()>MAX_FLIGHTS)return;
        current.add(origin);
        for(String string: adjencencies.get(origin)){
            if(!current.contains(string)){
                List<String> newCurrent=new ArrayList<>(current);
                newCurrent.add(string);
                if(string.equals(destination)){
                    added.add(newCurrent);
                }
                else depthFirst(origin, destination, added, newCurrent);
            }
        }
    }
}
