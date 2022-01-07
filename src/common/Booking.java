package common;

import common.Exceptions.IncompatibleFlightsException;
import common.Exceptions.MaxFlightsException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

//Viagem, guarda voos que representam uma ou mais escalas que o utilizador irá realizar
public class Booking {

    private String bookingID;
    private String clientID;
    private LocalDate date;
    private StopOvers stopOvers;//Escalas

    public Booking(String clientID,LocalDate date){
        bookingID = Helpers.randomString();
        this.clientID=clientID;
        this.date=date;
        stopOvers=new StopOvers();
    }

    //Construtor com escalas
    public Booking(String clientID,LocalDate date,List<Flight> stopOvers) throws IncompatibleFlightsException, MaxFlightsException {
        bookingID=Helpers.randomString();
        this.clientID=clientID;
        this.date=date;
        if(stopOvers==null)this.stopOvers=new StopOvers();
        else this.stopOvers=new StopOvers(stopOvers);
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
