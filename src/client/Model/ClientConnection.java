package client.Model;

import common.Credentials;
import common.Exceptions.DayClosedException;
import common.Exceptions.FlightFullException;
import common.Exceptions.FlightNotFoundException;
import common.Exceptions.WrongFrameTypeException;
import common.Frame;
import common.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class ClientConnection {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientConnection(String ip,int port) throws IOException {
        clientSocket=new Socket(ip,port);
        input=new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        output=new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
    }

    public int login(String username,char[]password) throws IOException,WrongFrameTypeException {
        Credentials credentials=new Credentials(username,password);
        output.write(credentials.createFrame().serialize());
        output.flush();
        Frame response=new Frame(input);
        if(response.getType()==Frame.BASIC){
            String resposta=new String(response.getData().get(0), StandardCharsets.UTF_8);
            if(resposta.equals("CLIENT"))return 1;
            else if(resposta.equals("ADMIN"))return 2;
            else return -1;
        }
        else throw new WrongFrameTypeException();
    }

    public String reservation(LocalDate date,StopOvers stopOvers) throws IOException, WrongFrameTypeException,DayClosedException,FlightFullException,FlightNotFoundException {
        Frame frame=new Frame(Frame.BOOKING);
        frame.addBlock(Helpers.localDateToBytes(date));
        frame.addBlock(stopOvers.createFrame().serialize());
        output.write(frame.serialize());
        output.flush();
        Frame response=new Frame(input);
        if(response.getType()==0){
            String resposta=new String(response.getData().get(0), StandardCharsets.UTF_8);
            switch(resposta){
                case "NOT FOUND":throw new FlightNotFoundException();
                case "FULL":throw new FlightFullException();
                case "DAY CLOSED":throw new DayClosedException();
                default:return resposta;
            }
        }
        else throw new WrongFrameTypeException();
    }

    public void close(){
        try{
            clientSocket.close();
            input.close();
            output.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
