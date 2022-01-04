package common.Exceptions;

public class DuplicateClient extends AccountException{
    public DuplicateClient(){
        super();
    }
    public DuplicateClient(String message){
        super(message);
    }
}
