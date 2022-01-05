package common.Exceptions;

public class FlightNotFoundException extends FlightException{

    public FlightNotFoundException(){
        super();
    }

    public FlightNotFoundException(String message){
        super(message);
    }
}
