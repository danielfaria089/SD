package common.Exceptions;

public class BookingNotFound extends Exception{
    public BookingNotFound(){
        super();
    }
    public BookingNotFound(String message){
        super(message);
    }
}
