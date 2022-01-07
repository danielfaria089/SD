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
    private Set<String> flights;
    private Set<String> notifications;

    public Account(String u, char[] p, boolean b){
        this.username = u;
        this.password = p.clone();
        this.admin = b;
        this.flights = new TreeSet<>();
        this.notifications=new TreeSet<>();
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

    public void addBooking(String bookingID){
        flights.add(bookingID);
    }

    public void removeBooking(String id){
        flights.remove(id);
    }

    public Set<String> getBookingsIds(){
        return new TreeSet<>(flights);
    }
}
