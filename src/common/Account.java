package common;

import common.Exceptions.WrongFrameTypeException;
import common.Flight;
import server.Flights;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class Account {

    private String username;
    private char[] password;
    private boolean admin;//true=admin  false==client
    private Map<String, Pair<Flight,LocalDate>> flights;

    public Account(String u, char[] p, boolean b){
        this.username = u;
        this.password = p.clone();
        this.admin = b;
        this.flights = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void addFlight(Flight f, LocalDate date){
        flights.put(f.getId(), new Pair<>(f,date));
    }

    public void removeFlight(String id){
        flights.remove(id);
    }

    public List<Pair<String,LocalDate>> getBookingsIds() throws IOException {
        List<Pair<String,LocalDate>> ret = new ArrayList<>();
        for(Map.Entry<String,LocalDate> p : flights.entrySet()){
            ret.add(new Pair<>(p.getKey(),p.getValue()));
        }
        return ret;
    }

    public void readAccountFlights(List<Frame> frames) throws WrongFrameTypeException{
        for(Frame frame : frames){
            if(frame.getType()!=(byte)5) throw new WrongFrameTypeException();
            List<byte[]> bytes = frame.getData();
            Flight flight = new Flight();
            flight.readFrame(frame);
            flights.put(flight.getId(), new Pair<>(flight,Helpers.localDateFromBytes(bytes.get(1))));
        }
    }
}
