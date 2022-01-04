package common;

import common.Exceptions.FlightFull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Flight {

    private String id;
    private String origin;
    private String destination;
    private int capacity;
    private Set<String> accountIds;//Ids dos utilizadores que marcaram voo

    public Flight(String i, String o, String d, int c){
        this.id = i;
        this.origin = o;
        this.destination = d;
        this.capacity = c;
        this.accountIds = new TreeSet<>();
    }

    public Flight(Flight flight){
        this.id = flight.id;
        this.origin= flight.origin;
        this.destination=flight.destination;
        this.capacity=flight.capacity;
        this.accountIds =new TreeSet<>(flight.accountIds);
    }

    public String getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupation() {
        return accountIds.size();//Quantidade de utilizadores
    }

    public void addPassenger(Account c) throws FlightFull {
        if(this.checkOccupation()) accountIds.add(c.getUsername());
        else throw new FlightFull();
    }

    public void addPassenger(String id) throws FlightFull {
        if(this.checkOccupation()) accountIds.add(id);
        else throw new FlightFull();
    }

    public void removeClient(Account c){
        accountIds.remove(c.getUsername());
    }

    public void removeClient(String id){
        accountIds.remove(id);
    }

    public List<String> getClients(){
        return new ArrayList<>(accountIds);
    }

    public boolean checkOccupation(){
        return accountIds.size() < capacity;
    }

    public Flight clone(){
        return new Flight(this);
    }

    public int compareFlight(Flight flight){
        if(origin==null && flight.origin!=null)return -1;
        if(origin!=null && flight.origin==null)return 1;
        if(origin == null)return 0;
        if(flight.origin.compareTo(this.origin)==0) {
            return flight.destination.compareTo(this.destination);
        }
        else return flight.origin.compareTo(this.origin);
    }

    public Frame createFrame(){
        Frame frame = new Frame((byte) 2);
        frame.addBlock(id.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(origin.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(destination.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(Helpers.intToByteArray(capacity));
        return frame;
    }

    public void readFrame(Frame frame){
        if(frame.getType()!='2') return;
        List<byte[]> bytes = frame.getData();
        id = new String(bytes.get(0),StandardCharsets.UTF_8);
        origin = new String(bytes.get(1),StandardCharsets.UTF_8);
        destination = new String(bytes.get(2),StandardCharsets.UTF_8);
        capacity = Helpers.intFromByteArray(bytes.get(3));
    }
}
