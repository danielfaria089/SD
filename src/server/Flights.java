package server;

import common.Exceptions.*;
import common.Flight;
import common.Booking;

import java.util.*;

//Voos de um certo dia
public class Flights {

    public static final int MAX_FLIGHTS=3;//Número máximo de escalas

    private Map<String,Flight> flights; //Voos do dia

    private boolean closed; //Se o dia está fechado ou não

    //Construtor com voos do dia e adjacências de cidades
    public Flights(Set<Flight> flights){
        this.flights=new HashMap<>();
        for(Flight flight:flights){
            this.flights.put(flight.getId(), flight.clone());
        }
        closed=false;
    }

    //Adiciona uma viagem ao dia
    public void addBooking(Booking booking) throws FlightFullException, FlightNotFoundException, DayClosedException {
        if(closed)throw new DayClosedException();
        String clientID=booking.getClientID();
        for(String flightID: booking.getFlightIDs()){
            Flight f=flights.get(flightID);
            if(f==null)throw new FlightNotFoundException();
            if(!f.checkOccupation())throw new FlightFullException();
        }
        for(String flightID: booking.getFlightIDs()){
            flights.get(flightID).addPassenger(clientID);
        }
    }

    public void cancelBooking(Booking booking){
        String clientID= booking.getClientID();
        for(String flightID: booking.getFlightIDs()){
            flights.get(flightID).removePassenger(clientID);
        }
    }

    public boolean isClosed(){
        return closed;
    }

    public void cancelDay(){
        for(Flight f:flights.values()){
            f.removeAllPassengers();
        }
        closed=true;
    }

    public boolean contains(String id){
        return flights.containsKey(id);
    }

    public boolean contains(Flight flight){
        return flights.containsKey(flight.getId());
    }

    public Flight getFlight(String origin, String dest){
        for(Flight f : flights.values()){
            if(f.equals(origin,dest) && f.checkOccupation())
                return f.clone();
        }
        return null;
    }

    public void addPassenger(String idCliente, List<Flight> stopOvers) throws FlightFullException, DayClosedException {
        if(this.closed){
            throw new DayClosedException();
        }
        for(Flight f : stopOvers){
            flights.get(f.getId()).addPassenger(idCliente);
        }
    }

    //Adiciona um voo default
    public void addDefaultFlight(Flight flight){
        flights.put(flight.getId(),flight);
    }
}
