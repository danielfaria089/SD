package common;

import common.Exceptions.IncompatibleFlightsException;
import common.Exceptions.MaxFlightsException;
import common.Exceptions.WrongFrameTypeException;
import server.Flights;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StopOvers {

    private List<Flight> stopOvers;

    public StopOvers(){
        stopOvers=new ArrayList<>();
    }

    public StopOvers(List<Flight> flights) throws IncompatibleFlightsException, MaxFlightsException {
        stopOvers=new ArrayList<>();
        for(Flight flight:flights){
            addFlight(flight);
        }
    }

    public StopOvers(StopOvers stopOvers){
        this.stopOvers=stopOvers.getStopOvers();
    }

    public StopOvers(Frame frame) throws IOException, WrongFrameTypeException {
        readFrame(frame);
    }

    public Frame createFrame() throws IOException {
        Frame frame=new Frame((byte)3);
        for(Flight flight:stopOvers){
            frame.addBlock(flight.createFrame().serialize());
        }
        return frame;
    }

    public void readFrame(Frame frame)throws IOException, WrongFrameTypeException {
        if(frame.getType()!=(byte)3)throw new WrongFrameTypeException();
        List<byte[]>data=frame.getData();
        stopOvers=new ArrayList<>();
        for(int i=0;i<data.size();i++){
            byte[]block=data.get(i);
            Flight flight=new Flight(new Frame(block));
            stopOvers.add(flight);
        }
    }

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

    public List<Flight> getStopOvers(){
        return stopOvers.stream().map(Flight::clone).collect(Collectors.toList());
    }

    public Set<String> getFlightIDs(){
        return stopOvers.stream().map(Flight::getId).collect(Collectors.toSet());
    }

    public void addFlight(Flight flight) throws IncompatibleFlightsException, MaxFlightsException {
        if(stopOvers==null)throw new NullPointerException();
        else if(stopOvers.isEmpty())stopOvers.add(flight);
        else if(stopOvers.size()>=Flights.MAX_FLIGHTS)throw new MaxFlightsException();
        else{
            if(!this.getDestination().equals(flight.getOrigin()))throw new IncompatibleFlightsException();
            else stopOvers.add(flight);
        }
    }

    public int compare(StopOvers booking){
        if(stopOvers==null && booking.stopOvers!=null)return -1;
        if(stopOvers==null)return 0;
        if(booking.stopOvers==null)return 1;
        Flight flight1;
        Flight flight2;
        for(int i = 0; i< Flights.MAX_FLIGHTS; i++){
            flight1=stopOvers.get(i);
            flight2= booking.stopOvers.get(i);
            if(flight1==null&&flight2!=null)return -1;
            else if(flight1==null)return 0;
            else if(flight2==null)return 1;
            if(flight1.compareFlight(flight2)!=0)return flight1.compareFlight(flight2);
        }
        return 0;
    }

    public StopOvers clone(){
        return new StopOvers(this);
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        if(stopOvers.size()>=1)sb.append(stopOvers.get(0).getOrigin());
        for(Flight f:stopOvers){
            sb.append(" -> ").append(f.getDestination());
        }
        return sb.toString();
    }
}
