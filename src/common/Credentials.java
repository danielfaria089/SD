package common;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Credentials {

    private String username;
    private char[] password;

    public Credentials(String username,char[] password){
        this.username=username;
        this.password= Arrays.copyOf(password,password.length);
    }

    public Credentials(Frame frame){
        this.readFrame(frame);
    }

    public Frame createFrame(){
        Frame frame=new Frame((byte) 1);
        frame.addBlock(username.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(Helpers.charToBytes(password));
        return frame;
    }

    public void readFrame(Frame frame){
        if(frame.getType()==(byte)1){
            List<byte[]> data=frame.getData();
            username=new String(data.get(0),StandardCharsets.UTF_8);
            password=Helpers.bytesToChar(data.get(1));
        }
    }

    public String getUsername(){
        return username;
    }

    public char[] getPassword(){
        return Arrays.copyOf(password,password.length);
    }
}
