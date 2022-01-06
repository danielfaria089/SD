package common.Exceptions;

public class IncompatibleFlightsException extends FlightException{
    public IncompatibleFlightsException(){
        super();
    }
    public IncompatibleFlightsException(String message){
        super(message);
    }
}
