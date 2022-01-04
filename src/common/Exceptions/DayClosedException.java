package common.Exceptions;

public class DayClosedException extends Exception{
    public DayClosedException(){
        super();
    }

    public DayClosedException(String message){
        super(message);
    }
}
