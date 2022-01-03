package common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flight {

    private String id;
    private String origin;
    private String destination;
    private int capacity;
    private int occupation;
    private Map<String, Account> accounts;

    public Flight(String i, String o, String d, int c, int occupation){
        this.id = i;
        this.origin = o;
        this.destination = d;
        this.capacity = c;
        this.occupation = occupation;
        this.accounts = new HashMap<>();
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
        return occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public void addClient(Account c){
        accounts.put(c.getUsername(),c);
    }

    public void removeClient(Account c){
        accounts.remove(c.getUsername());
    }

    public List<Account> getListClients(){
        return new ArrayList<>(accounts.values());
    }

    public boolean checkOccupation(){
        return occupation > 0;
    }
}
