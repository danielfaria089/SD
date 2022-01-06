package common;

import common.Exceptions.IncompatibleFlightsException;
import common.Exceptions.MaxFlightsException;
import common.Exceptions.WrongFrameTypeException;
import server.Flights;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

//Viagem, guarda voos que representam uma ou mais escalas que o utilizador irá realizar
public class Booking {

    private String bookingID;
    private String clientID;
    private LocalDate date;
    private StopOvers stopOvers;//Escalas

    public Booking(String clientID,LocalDate date){
        bookingID = Helpers.randomString();
        this.clientID=clientID;
        stopOvers=new StopOvers();
    }

    //Construtor com escalas
    public Booking(String clientID,LocalDate date,List<Flight> stopOvers) throws IncompatibleFlightsException, MaxFlightsException {
        bookingID=Helpers.randomString();
        this.clientID=clientID;
        this.date=date;
        if(stopOvers==null)throw new NullPointerException();
        this.stopOvers=new StopOvers(stopOvers);
    }

    public String getClientID(){
        return clientID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public LocalDate getDate(){
        return date;
    }

    public Set<String> getFlightIDs(){
        return stopOvers.getFlightIDs();
    }

    public int compare(Booking booking){
        return bookingID.compareTo(booking.getBookingID());
    }

}
