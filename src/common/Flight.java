package common;

import server.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flight {

    private String id;
    private String origin;
    private String destination;
    private LocalDate date;
    private int capacity;
    private int occupation;
    private Map<String, Client> clients;

    public Flight(String i, String o, String d, int c, int occupation){
        this.id = i;
        this.origin = o;
        this.destination = d;
        this.date = LocalDate.now();
        this.capacity = c;
        this.occupation = occupation;
        this.clients = new HashMap<>();
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

    public LocalDate getDate() {
        return date;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupation() {
        return occupation;
    }

    public void addClient(Client c){
        clients.put(c.getUsername(),c);
    }

    public void removeClient(Client c){
        clients.remove(c.getUsername());
    }

    public List<Client> getListClients(){
        return new ArrayList<>(clients.values());
    }

    public boolean checkOccupation(){
        return occupation > 0;
    }
}
