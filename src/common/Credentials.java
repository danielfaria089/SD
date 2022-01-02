package common;

import java.nio.charset.StandardCharsets;

public class Credentials extends Frame{

    public Credentials(String username, char[] password){
        super((byte)1);
        this.addBlock(username.getBytes(StandardCharsets.UTF_8));
        this.addBlock(new String(password).getBytes(StandardCharsets.UTF_8));
    }
}
