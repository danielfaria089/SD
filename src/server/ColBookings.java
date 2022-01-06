package server;

import common.Account;
import common.Booking;
import common.Exceptions.DayClosedException;
import common.Exceptions.FlightFullException;
import common.Exceptions.FlightNotFoundException;
import common.StopOvers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ColBookings {
    private Map<String, Booking> reservations;
    private Map<LocalDate,Flights> flightsMap;
    private FlightCalculator flightCalculator;

    public ColBookings(FlightCalculator calculator) throws IOException {
        flightCalculator=calculator;
    }

    public void addBooking(Booking booking) throws FlightNotFoundException, DayClosedException, FlightFullException {
        reservations.put(booking.getBookingID(),booking);
        LocalDate date=booking.getDate();
        if(!flightsMap.containsKey(date))flightsMap.put(date,new Flights(flightCalculator.getDefaultFlights()));
        flightsMap.get(date).addBooking(booking);
    }

    public void cancelBooking(String id){
        Booking booking=reservations.remove(id);
        flightsMap.get(booking.getDate()).cancelBooking(booking);
    }

    public Set<Booking> getBookings(Account account){
        Set<Booking> bookings=new TreeSet<>(Booking::compare);
        for(String bookingID:account.getBookingsIds()){
            bookings.add(reservations.get(bookingID));
        }
        return bookings;
    }
}
