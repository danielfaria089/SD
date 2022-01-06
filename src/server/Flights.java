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
            try{
                flights.get(flightID).addPassenger(clientID);
            }catch (NullPointerException e){
                throw new FlightNotFoundException(e.getMessage());
            }
        }
    }

    public void cancelBooking(Booking booking){
        String clientID= booking.getClientID();
        for(String flightID: booking.getFlightIDs()){
            flights.get(flightID).removeClient(clientID);
        }
    }

    //Adiciona um voo default
    public void addDefaultFlight(Flight flight){
        flights.put(flight.getId(),flight);
    }
}
