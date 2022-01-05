package common.Exceptions;

public class DuplicateClientException extends AccountException{
    public DuplicateClientException(){
        super();
    }
    public DuplicateClientException(String message){
        super(message);
    }
}
