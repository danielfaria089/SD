package common.Exceptions;

public class FlightNotFound extends FlightException{

    public FlightNotFound(){
        super();
    }

    public FlightNotFound(String message){
        super(message);
    }
}
