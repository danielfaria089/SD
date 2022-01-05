package common.Exceptions;

public class MaxFlightsException extends FlightException{
    public MaxFlightsException(){
        super();
    }
    public MaxFlightsException(String message){
        super(message);
    }
}
