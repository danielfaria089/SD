package common;

import common.Exceptions.FlightFullException;
import common.Exceptions.WrongFrameTypeException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    public Lock l = new ReentrantLock();

    private String id;
    private String origin;
    private String destination;
    private int capacity;
    private Set<String> accountIds;//Ids dos utilizadores que marcaram voo

    public Flight(){
        this.id="";
        this.origin = "";
        this.destination = "";
        this.capacity = 0;
        this.accountIds = new TreeSet<>();
    }

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

    public Flight(Frame frame)throws WrongFrameTypeException{
        this.readFrame(frame);
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

    public void addPassenger(Account c) throws FlightFullException {
        if(this.checkOccupation()) accountIds.add(c.getUsername());
        else throw new FlightFullException();
    }

    public void addPassenger(String id) throws FlightFullException {
        if(this.checkOccupation()) accountIds.add(id);
        else throw new FlightFullException();
    }

    public void removePassenger(Account c){
        accountIds.remove(c.getUsername());
    }

    public void removePassenger(String id){
        accountIds.remove(id);
    }

    public void removeAllPassengers(){
        this.accountIds=new TreeSet<>();
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

    public boolean equals(Flight flight){
        return this.compareFlight(flight)==0;
    }

    public boolean equals(String o,String d){
        return o.equals(origin) && d.equals(destination);
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
        Frame frame = new Frame(Frame.FLIGHT);
        frame.addBlock(id.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(origin.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(destination.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(Helpers.intToByteArray(capacity));
        return frame;
    }

    public Flight readFrame(Frame frame)throws WrongFrameTypeException{
        if(frame.getType()!=Frame.FLIGHT) throw new WrongFrameTypeException();
        List<byte[]> bytes = frame.getData();
        id = new String(bytes.get(0),StandardCharsets.UTF_8);
        origin = new String(bytes.get(1),StandardCharsets.UTF_8);
        destination = new String(bytes.get(2),StandardCharsets.UTF_8);
        capacity = Helpers.intFromByteArray(bytes.get(3));
        return new Flight(id,origin,destination,capacity);
    }
}
