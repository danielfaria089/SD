package server;

import common.Account;
import common.Exceptions.*;
import common.Flight;
import common.Booking;
import common.StopOvers;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataBase {

    private static final String FLIGHTS_FILE="src/server/Files/Flights";

    private ColBookings bookings;
    private Map<String, Account> accounts;

    private ReadWriteLock l = new ReentrantReadWriteLock();
    private Lock l_r = l.readLock();
    private Lock l_w = l.writeLock();

    //Construtors
    public DataBase(){
        try{
            accounts=new HashMap<>();
            bookings=new ColBookings(new FlightCalculator(FLIGHTS_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //FUNCIONALIDADE 1:DONE

    public void addClient(String username,char[]password, boolean admin) throws AccountException {
        l_w.lock();
        try {
            if(accounts.containsKey(username))throw new AccountException();
            else{
                accounts.put(username,new Account(username,password,admin));
            }
        }
        finally{
            l_w.unlock();
        }
    }

    public void addClient(Account c) throws AccountException {
        c.l.lock();
        l_w.lock();
        try {
            if(accounts.containsKey(c.getUsername()))throw new AccountException();
            else{
                accounts.put(c.getUsername(),new Account(c.getUsername(),c.getPassword(),c.isAdmin()));
            }
        }
        finally{
            c.l.unlock();
            l_w.unlock();
        }
    }

    public boolean checkLogIn(String id, char[] pass){
        l_r.lock();
        try{
            Account c = accounts.get(id);
            c.l.lock();
            try{
                return Arrays.equals(c.getPassword(), pass);
            }finally {
                c.l.unlock();

            }
        }
        finally{
            l_r.unlock();
        }
    }

    //FUNCIONALIDADE 2:DONE

    public boolean checkAdmin(String id) throws AccountException {
        l_r.lock();
        try{
            if(!accounts.containsKey(id)) throw new AccountException();
            else{
                Account c = accounts.get(id);
                c.l.lock();
                try {
                    return c.isAdmin();
                }finally {
                    c.l.unlock();
                }
            }
        }
        finally {
            l_r.unlock();
        }
    }

    //FUNCIONALIDADE 3:DONE

    public void addDefaultFlight(Flight flight) throws FlightException {
            bookings.addDefaultFlight(flight);
    }

    //FUNCIONALIDADE 4: DONE

    public void cancelDay(LocalDate date) throws DateTimeException{
        try{
            Set<String> cancelledBookings= bookings.cancelDay(date,l_w);
            for(String notif : cancelledBookings){
                String[] notificacao = notif.split(" ");
                accounts.get(notificacao[0]).adicionarNotificacao(notificacao[1]);
            }
        }finally {
            l_w.unlock();
        }
    }

    public void adicionarNotificacaoACliente(String id, String not){
        l_r.lock();
        try{
            Account c = accounts.get(id);
            c.l.lock();
            try {
                c.adicionarNotificacao(not);
            }finally {
                c.l.unlock();
            }
        }finally {
            l_r.unlock();
        }
    }

    public Set<String> getNotificacoesCliente(String id){
        l_r.lock();
        try {
            Account c = accounts.get(id);
            c.l.lock();
            try {
                return c.getNotifications(true);
            }finally {
                c.l.unlock();
            }
        }finally {
            l_r.unlock();
        }
    }

    //FUNCIONALIDADE 5 mix ADICIONAL 1:

    public Set<String> getAllCities(){
        return bookings.getAllCities();
    }

    public Set<Booking> possibleBookings(String origin, String destination, LocalDate start, LocalDate end){
        if(start.isAfter(end))return null;
        List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                            .limit(ChronoUnit.DAYS.between(start, end)+1)
                            .collect(Collectors.toList());
        return bookings.getPossibleBookings(origin,destination,dates);
    }

    public void addBooking(Booking booking) throws FlightFullException, FlightNotFoundException, DayClosedException {
        bookings.addBooking(booking);
    }

    public String registerBooking(String idCliente,List<String> percurso, LocalDate start, LocalDate end){ // Funcionalidade 5
        if(start.isAfter(end)) return null;
        List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end))
                .collect(Collectors.toList());
        return bookings.getFirstBooking(idCliente,percurso,dates);
    }

    //FUNCIONALIDADE 6:

    public void cancelBooking(String bookingID,String clientID) throws BookingNotFound, DayClosedException, AccountException {
        l_r.lock();
        try {
            bookings.cancelBooking(bookingID, clientID);
        }finally {
            l_w.lock();
            l_r.unlock();
        }
        try {
            this.accounts.get(clientID).removeBooking(bookingID);
        }finally {
            l_w.unlock();
        }
    }

    public List<Flight> getFlightsFromBooking(String id){
        return bookings.getFlightsFromBooking(id);
    }

    public Account getAccount(String id){
        l_r.lock();
        try {
            return accounts.get(id).clone();
        }finally {
            l_r.unlock();
        }
    }

    //FUNCIONALIDADE 7:


    public Set<Flight> getDefaultFlights() throws IOException {
        return bookings.getDefaultFlights();
    }

    //OTHERS

    public void clearOldFlights(){
        bookings.clearOldFlights();
    }

}

/*
    public DataBase(){
        this.flights = new HashMap<>();
        this.accounts = new HashMap<>();
        this.stopOver = new HashMap<>();
    }

    public void addFligth(String o, String d, int c){
        String random = Helpers.randomString();
        Flight f = new Flight(random,o,d,c);
        flights.put(random,f);
    }

    public void removeFligth(String id){
        flights.remove(id);
        for(Account c : accounts.values()){
            c.removeFlight(id);
        }
    }

    public void addClient(String u, char[] p){
        Account c = new Account(u, p);
        accounts.put(u,c);
    }

    public boolean checkLogIn(String id, char[] pass){
        return Arrays.equals(accounts.get(id).getPassword(), pass);
    }

    public List<Flight> getSameOrigin(String origin){
        List<Flight> ret = new ArrayList<>();
        for(Flight f : flights.values()){
            if(f.getOrigin().equals(origin)) ret.add(f);
        }
        return ret;
    }

    public void addStopOver(Flight f){
        List<Flight> aux = new ArrayList<>();
        for(Flight flight : flights.values()){
            stopOver.put(Helpers.randomString(), getSameOrigin(f.getDestination()));
        }
    }

    public String getStopOverOrigin(String id){
        return stopOver.get(id).get(0).getOrigin();
    }

    public String getStopOverDestination(String id){
        int length = stopOver.get(id).size();
        return stopOver.get(id).get(length-1).getDestination();
    }

    public void checkStopOver(String id){
        if(getStopOverOrigin(id).equals(getStopOverDestination(id))) stopOver.remove(id);
        if(stopOver.get(id).size()>3) stopOver.remove(id);
    }

    public Map<String, List<Flight>> getFlightsWithOrigin(String origin){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if(f.getOrigin().equals(origin)) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if(f.getOrigin().equals(origin)) ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }

    public Map<String, List<Flight>> getFlightsWithDestination(String destination){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if(f.getDestination().equals(destination)) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if(f.getDestination().equals(destination)) ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }

    public Map<String, List<Flight>> getFlightsWithOriginAndDestination(String origin, String destination){
        Map<String, List<Flight>> ret = new HashMap<>();
        for(Flight f : flights.values()){
            if((f.getDestination().equals(destination))&&(f.getOrigin().equals(origin))) {
                List<Flight> aux = new ArrayList<>();
                aux.add(f);
                ret.put(f.getId(),aux);
            }
        }

        for(Map.Entry<String,List<Flight>> map : stopOver.entrySet()){
            for(Flight f : map.getValue()){
                if((f.getDestination().equals(destination))&&(f.getOrigin().equals(origin)))
                    ret.put(map.getKey(), map.getValue());
            }
        }

        return ret;
    }

    public void dayClosure(){
        for(Flight f : flights.values()){
            f.setOccupation(f.getCapacity());
            for(Account acc : accounts.values()){
                acc.removeFlight(f.getId());
                f.removeClient(acc);
            }
        }
    }
 */
