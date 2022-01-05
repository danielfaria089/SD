package common.Exceptions;

public class WrongFrameTypeException extends Exception{
    public WrongFrameTypeException(){
        super();
    }
    public WrongFrameTypeException(String message){
        super(message);
    }
}
