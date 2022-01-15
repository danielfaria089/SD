package server;

import common.Account;
import common.Booking;
import common.Exceptions.*;
import common.Flight;
import common.StopOvers;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ColBookings {
    private Map<String, Booking> reservations;
    private Map<LocalDate,Flights> flightsMap;
    private FlightCalculator flightCalculator;

    private ReadWriteLock l = new ReentrantReadWriteLock();
    private Lock l_w = l.writeLock();
    private Lock l_r = l.readLock();

    public ColBookings(FlightCalculator calculator) throws IOException, FlightNotFoundException, DayClosedException, MaxFlightsException, IncompatibleFlightsException, FlightFullException {
        flightCalculator=calculator;
        reservations = new HashMap<>();
        flightsMap = new TreeMap<>(LocalDate::compareTo);
    }

    public Set<Booking> getPossibleBookings(String origin, String destination, List<LocalDate> dates){
        l_r.lock();
        try {
            List<StopOvers> stopOversList = flightCalculator.getFlights(origin, destination);
            Set<Booking> bookings = new TreeSet<>(Comparator.comparing(Booking::getBookingID));
            for (LocalDate date : dates) {
                if(!flightsMap.containsKey(date)) {
                    flightsMap.put(date, new Flights(flightCalculator.getDefaultFlights()));
                }
                Flights flights = flightsMap.get(date);
                if (flights!=null&&!flights.isClosed()) {
                    for (StopOvers stopOver : stopOversList) {
                        if(flights.containsAll(stopOver.getStopOvers()))
                        try {
                            bookings.add(new Booking(date, stopOver.getStopOvers()));
                        } catch (IncompatibleFlightsException | MaxFlightsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return bookings;
        }finally {
            l_r.unlock();
        }

    }

    public String getFirstBooking(String idCliente,List<String> percurso, List<LocalDate> dates) throws IncompatibleFlightsException, MaxFlightsException, FlightFullException, DayClosedException, FlightNotFoundException {
        l_w.lock();
        try {
            for (LocalDate date : dates) {
                if(!flightsMap.containsKey(date)) {
                    flightsMap.put(date, new Flights(flightCalculator.getDefaultFlights()));
                }
                Flights flights = flightsMap.get(date);
                List<Flight> stopOvers = new ArrayList<>();
                int i;
                for (i = 1; i < percurso.size(); i++) {
                    Flight f = flights.getFlight(percurso.get(i - 1),
                                                 percurso.get(i));
                    if (f == null) {
                        break;
                    }
                    stopOvers.add(f);
                }

                if (i == percurso.size()) {
                    Booking b = new Booking(idCliente, date, stopOvers);
                    addBooking(b);
                    return b.getBookingID();
                }
            }
            return "Fn";
        }finally {
            l_w.unlock();
        }
    }

    public void addBooking(Booking booking) throws FlightNotFoundException, DayClosedException, FlightFullException {
        l_w.lock();
        try{
            reservations.put(booking.getBookingID(), booking);
            LocalDate date = booking.getDate();
            if (!flightsMap.containsKey(date))
                flightsMap.put(date, new Flights(flightCalculator.getDefaultFlights()));
            flightsMap.get(date).addBooking(booking);
        }finally {
            l_w.unlock();
        }
    }


    public void cancelBooking(String bookingID,String clientID) throws BookingNotFound, DayClosedException, AccountException {
        l_w.lock();
        try{
            if (!reservations.get(bookingID).getClientID().equals(clientID)) throw new AccountException();
            else {
                Booking booking = reservations.get(bookingID); // primeiro checkar
                Flights flights = flightsMap.get(booking.getDate());
                if (flights == null) throw new BookingNotFound();
                else if (flights.isClosed()) throw new DayClosedException();
                else {
                    reservations.remove(bookingID); // só depois remover
                    flights.cancelBooking(booking);
                }
            }
        }finally {
            l_w.unlock();
        }
    }

    public Set<Booking> getBookings(Account account){
        Set<Booking> bookings=new TreeSet<>(Booking::compare);
        for(String bookingID:account.getBookingsIds()){
            bookings.add(reservations.get(bookingID));
        }
        return bookings;
    }

    public List<Flight> getFlightsFromBooking(String id){
        l_r.lock();
        try {
            return reservations.get(id).getStopOvers().getStopOvers();
        }finally {
            l_r.unlock();
        }
    }

    public void addDefaultFlight(Flight flight) throws FlightException {
        flightCalculator.l_w.lock();
        try {
            flightCalculator.addDefaultFlight(flight);
        }finally {
            l_w.lock();
            flightCalculator.l_w.unlock();
        }
        try {
            for(Flights flights: flightsMap.values()){
                flights.addDefaultFlight(flight);
            }
        }finally {
            l_w.unlock();
        }
    }

    //Cancela as reservas e retorna-as para os seus clientes poderem ser notificados
    public Set<String> cancelDay(LocalDate date, Lock l_super){
        Set<String> notif = new TreeSet<>();
        l_w.lock();
        try{
            if(!flightsMap.containsKey(date)){
                flightsMap.put(date, new Flights(flightCalculator.getDefaultFlights()));
            }
            flightsMap.get(date).cancelDay();
            List<Booking> remove = new ArrayList<>();
            for(Booking booking:reservations.values()){
                if(booking.getDate().equals(date)) {
                    notif.add(booking.createNotification());
                    remove.add(reservations.get(booking.getBookingID()));
                }
            }
            for(Booking b : remove){
                reservations.remove(b.getBookingID());
            }
            return notif;
        }finally {
            l_super.lock();
            l_w.unlock();
        }
    }

    public List<Booking> clearOldFlights(){
        List<LocalDate> rem_data = new ArrayList<>();
        List<Booking> rem_book = new ArrayList<>();
        for(LocalDate day: flightsMap.keySet()){
            if(day.isBefore(LocalDate.now().minusDays(1)))rem_data.add(day);//Minus para nao eliminar voos que ainda nao ocorreram em outras timezones
        }
        for(Booking booking: reservations.values()){
            if(booking.getDate().isBefore(LocalDate.now().minusDays(1)))rem_book.add(reservations.get(booking.getBookingID()));
        }
        for(LocalDate d : rem_data){
            flightsMap.remove(d);
        }
        for (Booking b : rem_book){
            reservations.remove(b.getBookingID());
        }
        return rem_book;
    }

    public Set<String> getAllCities(){
        return flightCalculator.getAllCities();
    }

    public Set<Flight> getDefaultFlights(){
        return flightCalculator.getDefaultFlights();
    }

    public Flight getDefaultFlight(String id){
        Flight flight = new Flight();
        for(Flight f : getDefaultFlights()){
            if(f.getId().equals(id)) flight = f;
        }
        return flight;
    }

    public void writeBookings(String filename) throws IOException {
        PrintWriter writer = new PrintWriter((new FileWriter(filename)));
        for(Booking b : reservations.values()){
            List<Flight> aux = new ArrayList<>(b.getStopOvers().getStopOvers());
            StringBuilder s = new StringBuilder();
            for(Flight f : aux){
                s.append(";").append(f.getId());
            }
            writer.println(b.getBookingID() + ";" +
                    b.getClientID() + ";" +
                    b.getDate() +
                    s);
        }
        writer.flush();
        writer.close();
    }

    public void writeFlights(String filename) throws IOException {
        flightCalculator.writeFlights(filename);
    }
}
