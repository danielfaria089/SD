package server;

import common.Credentials;
import common.Exceptions.*;
import common.Frame;
import common.*;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection implements Runnable{
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private DataBase dataBase;

    private String loggedUser;

    public ServerConnection(Socket socket,DataBase dataBase) throws IOException {
        this.socket=socket;
        this.dataBase=dataBase;
        input=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public boolean receive() throws IOException, WrongFrameTypeException, AccountException, IncompatibleFlightsException, MaxFlightsException {
        Frame frame=new Frame();
        frame.deserialize(input);
        switch (frame.getType()){
            case (byte)1:
                login(frame);//Recebe credentials e dá login
                break;
            case (byte)3:
                reservation(frame);//Recebe uma trip e regista os voos
                break;
            case (byte)4:
                allFlights();//Recebe uma trip e regista os voos
                break;
            case (byte)6:
                sendCities();//Envia a lista de cidades
                break;
            case (byte)7:

                break;
            default:
                return false;
        }
        return true;
    }

    private void login(Frame frame) throws IOException, WrongFrameTypeException, AccountException {
        Credentials credentials=new Credentials(frame);
        if(dataBase.checkLogIn(credentials.getUsername(), credentials.getPassword())){
            loggedUser=credentials.getUsername();
            Frame success=new Frame((byte)0);
            if(dataBase.checkAdmin(loggedUser)){
                success.addBlock("ADMIN".getBytes(StandardCharsets.UTF_8));
            }
            else success.addBlock("CLIENT".getBytes(StandardCharsets.UTF_8));
            output.write(success.serialize());
        }
        else{
            Frame failure=new Frame((byte)0);
            failure.addBlock("ERROR".getBytes(StandardCharsets.UTF_8));
            output.write(failure.serialize());
        }
        output.flush();
    }

    public void getPossibleBooking(Frame frame){
        String origin=new String(frame.getData().get(0),StandardCharsets.UTF_8);
        String destination=new String(frame.getData().get(1),StandardCharsets.UTF_8);
        LocalDate date=Helpers.localDateFromBytes(frame.getData().get(2));

    }

    //Frame que recebe: (byte)Type :(0) (LocalDate)data -> (1) (Trip)viagem
    private void reservation(Frame frame) throws IOException, IncompatibleFlightsException, MaxFlightsException, WrongFrameTypeException {
        List<byte[]>data=frame.getData();
        LocalDate date=LocalDate.parse(new String(data.get(0),StandardCharsets.UTF_8));
        StopOvers stopOvers =new StopOvers(new Frame(data.get(1)));
        Booking booking=new Booking(loggedUser,date,stopOvers.getStopOvers());
        try{
            dataBase.addBooking(booking);
            Frame success=new Frame((byte)0);
            success.addBlock(booking.getBookingID().getBytes(StandardCharsets.UTF_8));
            output.write(success.serialize());
        }catch (FlightNotFoundException e){
            Frame failure=new Frame((byte)0);
            failure.addBlock("NOT FOUND".getBytes(StandardCharsets.UTF_8));
            output.write(failure.serialize());
        }catch (FlightFullException e){
            Frame failure=new Frame((byte)0);
            failure.addBlock("FULL".getBytes(StandardCharsets.UTF_8));
            output.write(failure.serialize());
        }catch (DayClosedException e){
            Frame failure=new Frame((byte)0);
            failure.addBlock("DAY CLOSED".getBytes(StandardCharsets.UTF_8));
            output.write(failure.serialize());
        }
        output.flush();
    }

    private void allFlights() throws WrongFrameTypeException{
        try{
            Frame frame=new Frame((byte)4);
            List<Frame> frames = dataBase.createFlightsFrame();
            for(Frame f : frames){
                frame.addBlock(f.serialize());
            }
            output.write(frame.serialize());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    private void allBookings(){
        try {
            Frame frame = new Frame((byte)5);


        }
    }

    */
    public void sendCities()throws IOException{
        Frame frame=new Frame((byte)6);
        Set<String>cities=dataBase.getAllCities();
        for(String city:cities){
            frame.addBlock(city.getBytes(StandardCharsets.UTF_8));
        }
        output.write(frame.serialize());
        output.flush();
    }

    @Override
    public void run(){
        boolean run=true;
        try {
            while(run) {
                if (!receive()) run=false;
            }
            socket.close();
            output.close();
            input.close();
        } catch (IOException | WrongFrameTypeException | AccountException | IncompatibleFlightsException | MaxFlightsException e) {
            e.printStackTrace();
        }
    }
}
