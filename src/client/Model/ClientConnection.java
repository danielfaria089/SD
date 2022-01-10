package client.Model;

import common.Credentials;
import common.Exceptions.*;
import common.Exceptions.UnknownError;
import common.Frame;
import common.*;
import common.TaggedConnection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientConnection {
    private TaggedConnection tc;

    public ClientConnection(String ip,int port) throws IOException {
        Socket s =new Socket(ip,port);
        tc = new TaggedConnection(s);
    }

    public String[] login(String username,char[]password) throws IOException,WrongFrameTypeException {
        Credentials credentials=new Credentials(username,password);
        tc.send(credentials.createFrame());

        Frame response=tc.receive();
        if(response.getType()==Frame.BASIC){
            List<byte[]> resp = response.getData();
            String resposta=new String(resp.get(0), StandardCharsets.UTF_8);
            if(resposta.equals("CLIENT")){
                String[] res = new String[resp.size()];
                res[0] = "1";
                for(int i = 1; i < resp.size() ; i++){
                    String bookingId = new String(resp.get(i), StandardCharsets.UTF_8);
                    res[i] = bookingId;
                }
                return res;
            }
            else if(resposta.equals("ADMIN"))return new String[]{"2"};
            else return new String[]{"-1"};
        }
        else throw new WrongFrameTypeException();
    }

    public String reservation(LocalDate date,StopOvers stopOvers) throws IOException, WrongFrameTypeException, DayClosedException, FlightFullException, FlightNotFoundException, AccountException, WrongCredencials, UnknownError, BookingNotFound {
        Frame frame=new Frame(Frame.BOOKING);
        frame.addBlock(Helpers.localDateToBytes(date));
        frame.addBlock(stopOvers.createFrame().serialize());
        tc.send(frame);
        Frame response= tc.receive();
        if(response.getType()==Frame.BASIC){
            String resposta=new String(response.getData().get(0), StandardCharsets.UTF_8);
            trataErros(resposta);
            return resposta;
        }
        else throw new WrongFrameTypeException();
    }

    public List<Flight> allFlights() throws IOException, WrongFrameTypeException {
        Frame frame = new Frame((Frame.ALL_FLIGHTS));
        List<Flight> flights = new ArrayList<>();
        tc.send(frame);

        Frame response = tc.receive();
        if(response.getType()==Frame.ALL_FLIGHTS){
            for(byte[] b : frame.getData()){
                flights.add(new Flight(new Frame(b)));
            }
        }
        return flights;
    }

    public List<String> allCities() throws IOException {
        Frame frame = new Frame(Frame.CITIES);
        List<String> ret = new ArrayList<>();
        tc.send(frame);
        Frame response = tc.receive();
        if(response.getType()==Frame.CITIES){
            for(byte[] b : response.getData()){
                String aux = new String(b,StandardCharsets.UTF_8);
                ret.add(aux);
            }
        }
        return ret;
    }

    public List<String> getBookingsFromAccount() throws IOException, WrongFrameTypeException {
        Frame frame = new Frame(Frame.ACCOUNT_FLIGHTS);
        List<String> bookings = new ArrayList<>();
        tc.send(frame);
        Frame response = tc.receive();
        if(response.getType()==Frame.ACCOUNT_FLIGHTS){
            for(byte[] b : response.getData()){
                bookings.add(new String(b,StandardCharsets.UTF_8));
            }
        }
        return bookings;
    }

    public String[] pedeNotificacoes() throws IOException {
        tc.send(Frame.NOTIF,new ArrayList<>());


        List<byte[]> resp =tc.receive().getData();
        String[] res = new String[resp.size()];
        for(int i = 0; i < resp.size() ; i++){
            String bookingId = new String(resp.get(i), StandardCharsets.UTF_8);
            res[i] = bookingId;
        }
        return res;
    }

    public void cancelaBooking(String bookingId) throws IOException, FlightNotFoundException, DayClosedException, AccountException, WrongCredencials, UnknownError, BookingNotFound, FlightFullException {
        Frame frame = new Frame(Frame.CANCEL);
        frame.addBlock(bookingId.getBytes(StandardCharsets.UTF_8));
        tc.send(frame);

        List<byte[]> resp = tc.receive().getData();
        if(!resp.isEmpty()){
            trataErros(new String(resp.get(0),StandardCharsets.UTF_8));
        }
    }

    public void trataErros(String erro) throws AccountException, BookingNotFound, DayClosedException, FlightNotFoundException, FlightFullException, WrongCredencials, UnknownError {
        switch (erro){
            case "A":
                throw new AccountException();
            case "B":
                throw new BookingNotFound();
            case "D":
                throw new DayClosedException();
            case "Fn":
                throw new FlightNotFoundException();
            case "Ff":
                throw new FlightFullException();
            case "L":
                throw new WrongCredencials();
            case "?":
                throw new UnknownError();
            default:

        }
    }

    public void close(){
        try{
            tc.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
