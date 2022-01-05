package common.Exceptions;

public class ClientNotFoundException extends AccountException{
    public ClientNotFoundException(){
        super();
    }
    public ClientNotFoundException(String message){
        super(message);
    }
}
