package common.Exceptions;

public class FlightFull extends FlightException{
    public FlightFull(){
        super();
    }
    public FlightFull(String message){
        super(message);
    }
}
