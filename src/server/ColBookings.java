package server;

import common.Account;
import common.Booking;
import common.Exceptions.*;
import common.Flight;
import common.StopOvers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ColBookings {
    private Map<String, Booking> reservations;
    private Map<LocalDate,Flights> flightsMap;
    private FlightCalculator flightCalculator;

    private ReadWriteLock l = new ReentrantReadWriteLock();
    private Lock l_w = l.writeLock();
    private Lock l_r = l.readLock();

    public ColBookings(FlightCalculator calculator) throws IOException {
        flightCalculator=calculator;
    }

    public Set<Booking> getPossibleBookings(String origin, String destination, List<LocalDate> dates){
        Set<StopOvers> stopOversSet = flightCalculator.getFlights(origin,destination);
        Set<Booking> bookings=new TreeSet<>(Comparator.comparing(Booking::getDate));
        for(LocalDate date:dates){
            Flights flights=flightsMap.get(date);
            if(flights!=null&&!flights.isClosed()){
                for(StopOvers stopOver:stopOversSet){
                    boolean found=true;
                    for(Flight f:stopOver.getStopOvers()){
                        if(!flights.contains(f))found=false;
                    }
                    if(found){
                        try{
                            bookings.add(new Booking(date,stopOver.getStopOvers()));
                        } catch (IncompatibleFlightsException | MaxFlightsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return bookings;
    }

    public void addBooking(Booking booking) throws FlightNotFoundException, DayClosedException, FlightFullException {
        reservations.put(booking.getBookingID(),booking);
        LocalDate date=booking.getDate();
        if(!flightsMap.containsKey(date))flightsMap.put(date,new Flights(flightCalculator.getDefaultFlights()));
        flightsMap.get(date).addBooking(booking);
    }


    public void cancelBooking(String bookingID,String clientID) throws BookingNotFound, DayClosedException, AccountException {
        if(!reservations.get(bookingID).getClientID().equals(clientID))throw new AccountException();
        else {
            Booking booking=reservations.remove(bookingID);
            Flights flights=flightsMap.get(booking.getDate());
            if(flights==null)throw new BookingNotFound();
            else if(flights.isClosed())throw new DayClosedException();
            else flights.cancelBooking(booking);
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
        return reservations.get(id).getStopOvers().getStopOvers();
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
    public Set<Booking> cancelDay(LocalDate date){
        Set<Booking> bookings=new TreeSet<>(Booking::compare);
        for(Booking booking:reservations.values()){
            if(booking.getDate().equals(date)) {
                bookings.add(booking.clone());
                reservations.remove(booking.getBookingID());
            }
        }
        return bookings;
    }

    public void clearOldFlights(){
        for(LocalDate day: flightsMap.keySet()){
            if(day.isBefore(LocalDate.now().minusDays(1)))flightsMap.remove(day);//Minus para nao eliminar voos que ainda nao ocorreram em outras timezones
        }
        for(Booking booking: reservations.values()){
            if(booking.getDate().isBefore(LocalDate.now().minusDays(1)))reservations.remove(booking.getBookingID());
        }
    }

    public Set<String> getAllCities(){
        return flightCalculator.getAllCities();
    }

    public Set<Flight> getDefaultFlights(){
        return flightCalculator.getDefaultFlights();
    }

}
