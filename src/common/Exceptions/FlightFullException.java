package common.Exceptions;

public class FlightFullException extends FlightException{
    public FlightFullException(){
        super();
    }
    public FlightFullException(String message){
        super(message);
    }
}
