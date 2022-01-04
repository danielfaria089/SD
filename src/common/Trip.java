package common;

import server.Flights;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Viagem, guarda voos que representam uma ou mais escalas que o utilizador irá realizar
public class Trip {
    //Escalas
    private List<Flight> stopOvers;

    //Construtor nulo
    public Trip(){
        stopOvers=new ArrayList<>();
    }

    //Construtor com escalas
    public Trip(List<Flight> stopOvers){
        if(stopOvers==null)throw new NullPointerException();
        this.stopOvers=stopOvers.stream().map(Flight::clone).collect(Collectors.toList());
    }

    //Devolve origem
    public String getOrigin(){
        if(stopOvers==null)throw new NullPointerException();
        if(stopOvers.isEmpty())throw new IndexOutOfBoundsException();
        else return stopOvers.get(0).getOrigin();
    }

    //Devolve destino
    public String getDestination(){
        if(stopOvers==null)throw new NullPointerException();
        if(stopOvers.isEmpty())throw new IndexOutOfBoundsException();
        else return stopOvers.get(stopOvers.size()-1).getDestination();
    }

    //Adiciona um flight
    public void addFlight(Flight flight) throws IOException {
        if(stopOvers==null)throw new NullPointerException();
        if(stopOvers.size()>=Flights.MAX_FLIGHTS)throw new IndexOutOfBoundsException();//Criar excpetion para isto
        else{
            if(!this.getDestination().equals(flight.getOrigin()))throw new IOException();//criat tbm
            else stopOvers.add(flight);
        }
    }

    //Devolve escalas
    public List<Flight> getStopOvers(){
        return stopOvers.stream().map(Flight::clone).collect(Collectors.toList());
    }

    //Comparador de Trip
    public int compare(Trip trip){
        if(stopOvers==null && trip.stopOvers!=null)return -1;
        if(stopOvers==null)return 0;
        if(trip.stopOvers==null)return 1;
        Flight flight1;
        Flight flight2;
        for(int i=0;i<Flights.MAX_FLIGHTS;i++){
            flight1=stopOvers.get(i);
            flight2=trip.stopOvers.get(i);
            if(flight1.compareFlight(flight2)!=0)return flight1.compareFlight(flight2);
        }
        return 0;
    }
}
