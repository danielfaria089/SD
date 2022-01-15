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
import java.util.Set;
import java.util.TreeSet;

public class ClientConnection implements AutoCloseable {
    private Desmultiplexer dm;
    private String logged;

    public ClientConnection(String ip,int port) throws IOException {
        Socket s =new Socket(ip,port);
        dm = new Desmultiplexer(new TaggedConnection(s));
        dm.start();
    }

    public String[] login(String username,char[]password) throws IOException, WrongFrameTypeException, InterruptedException {
        Credentials credentials=new Credentials(username,password);
        dm.send(credentials.createFrame());

        Frame response=dm.receive(Frame.BASIC);
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
                logged=username;
                return res;
            }
            else if(resposta.equals("ADMIN")) {
                logged=username;
                return new String[]{"2"};
            }
            else return new String[]{"-1"};
        }
        else throw new WrongFrameTypeException();
    }

    public void registarCliente(String username,char[]password) throws IOException, WrongFrameTypeException, FlightNotFoundException, DayClosedException, AccountException, WrongCredentials, MaxFlightsException, UnknownError, BookingNotFound, IncompatibleFlightsException, FlightFullException, InterruptedException {
        Credentials credentials = new Credentials(username,password);
        dm.send(credentials.createFrame_Register());

        Frame response=dm.receive(Frame.BASIC);
        List<byte[]> data=response.getData();
        String res = new String(data.get(0),StandardCharsets.UTF_8);
        if(response.getType()==Frame.BASIC && data.size() == 1){
            trataErros(res);
            logged=username;
        }
        else throw new WrongFrameTypeException();
    }

    public Set<String> getCities() throws IOException, InterruptedException {
        Set<String> cities=new TreeSet<>();
        Frame frame=new Frame(Frame.CITIES);
        dm.send(frame);
        Frame response=dm.receive(Frame.CITIES);
        if(response.getType()==Frame.CITIES){
            List<byte[]> data=response.getData();
            for(byte[] block:data){
                cities.add(new String(block,StandardCharsets.UTF_8));
            }
        }
        return cities;
    }

    public List<Pair<LocalDate,StopOvers>> getPossibleBookings(String city1,String city2,LocalDate date1,LocalDate date2) throws IOException, InterruptedException {
        Frame frame=new Frame(Frame.STOPOVERS);
        frame.addBlock(city1.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(city2.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(Helpers.localDateToBytes(date1));
        frame.addBlock(Helpers.localDateToBytes(date2));
        dm.send(frame);
        Frame response=dm.receive(Frame.STOPOVERS);
        List<Pair<LocalDate,StopOvers>> possibleBooks=new ArrayList<>();
        List<byte[]>data=response.getData();
        try{
            for(int i=0;i<data.size();i+=2){
                possibleBooks.add(new Pair<>(Helpers.localDateFromBytes(data.get(i)),new StopOvers(new Frame(data.get(i+1)))));
            }
        } catch (WrongFrameTypeException e) {
            dm.send(frame);
        }
        return possibleBooks;
    }

    public String specificReservation(List<String>stopOvers,LocalDate dateBegin,LocalDate dateEnd) throws IOException, UnknownError, FlightNotFoundException, FlightFullException, WrongCredentials, AccountException, BookingNotFound, DayClosedException, WrongFrameTypeException, MaxFlightsException, IncompatibleFlightsException, InterruptedException {
        Frame frame=new Frame(Frame.SPEC_BOOK);
        frame.addBlock(Helpers.localDateToBytes(dateBegin));
        frame.addBlock(Helpers.localDateToBytes(dateEnd));
        for(String stop:stopOvers){
            frame.addBlock(stop.getBytes(StandardCharsets.UTF_8));
        }
        dm.send(frame);
        Frame response=dm.receive(Frame.BASIC);
        if(response.getType()==Frame.BASIC){
            String resposta=new String(response.getData().get(0), StandardCharsets.UTF_8);
            trataErros(resposta);
            return resposta;
        }
        else throw new WrongFrameTypeException();
    }

    public String reservation(StopOvers stopOvers,LocalDate date) throws IOException, WrongFrameTypeException, DayClosedException, FlightFullException, FlightNotFoundException, AccountException, WrongCredentials, UnknownError, BookingNotFound, MaxFlightsException, IncompatibleFlightsException, InterruptedException {
        Frame frame=new Frame(Frame.BOOKING);
        frame.addBlock(Helpers.localDateToBytes(date));
        frame.addBlock(stopOvers.createFrame().serialize());
        dm.send(frame);
        Frame response= dm.receive(Frame.BASIC);
        if(response.getType()==Frame.BASIC){
            String resposta=new String(response.getData().get(0), StandardCharsets.UTF_8);
            trataErros(resposta);
            return resposta;
        }
        else throw new WrongFrameTypeException();
    }

    public List<Flight> allFlights() throws IOException, WrongFrameTypeException, InterruptedException {
        Frame frame = new Frame((Frame.ALL_FLIGHTS));
        List<Flight> flights = new ArrayList<>();
        dm.send(frame);

        Frame response = dm.receive(Frame.ALL_FLIGHTS);
        if(response.getType()==Frame.ALL_FLIGHTS){
            for(byte[] b : response.getData()){
                flights.add(new Flight(new Frame(b)));
            }
        }
        return flights;
    }

    public List<String> getBookingsFromAccount() throws IOException, InterruptedException {
        Frame frame = new Frame(Frame.ACCOUNT_FLIGHTS);
        List<String> bookings = new ArrayList<>();
        dm.send(frame);
        Frame response = dm.receive(Frame.ACCOUNT_FLIGHTS);
        if(response.getType()==Frame.ACCOUNT_FLIGHTS){
            for(byte[] b : response.getData()){
                bookings.add(new String(b,StandardCharsets.UTF_8));
            }
        }
        return bookings;
    }

    public String[] pedeNotificacoes() throws IOException, InterruptedException {
        dm.send(Frame.NOTIF,new ArrayList<>());


        List<byte[]> resp =dm.receive(Frame.NOTIF).getData();
        String[] res = new String[resp.size()];
        for(int i = 0; i < resp.size() ; i++){
            String bookingId = new String(resp.get(i), StandardCharsets.UTF_8);
            res[i] = bookingId;
        }
        return res;
    }

    public String adicionaDefaultFlight(String origem, String destino, String capacidade) throws IOException, FlightNotFoundException, DayClosedException, AccountException, WrongCredentials, UnknownError, BookingNotFound, FlightFullException, MaxFlightsException, IncompatibleFlightsException, InterruptedException {
        Frame frame = new Frame(Frame.FLIGHT);
        frame.addBlock(origem.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(destino.getBytes(StandardCharsets.UTF_8));
        frame.addBlock(Helpers.intToByteArray(Integer.parseInt(capacidade)));
        dm.send(frame);

        List<byte[]> resp = dm.receive(Frame.BASIC).getData();
        String str = "Error";
        if(!resp.isEmpty()){
            str = new String(resp.get(0),StandardCharsets.UTF_8);
            trataErros(str);
        }
        return str;
    }

    public void cancelaDia(LocalDate date) throws IOException, FlightNotFoundException, DayClosedException, AccountException, WrongCredentials, UnknownError, BookingNotFound, FlightFullException, MaxFlightsException, IncompatibleFlightsException, InterruptedException {
        Frame frame = new Frame(Frame.CANCEL_DAY);
        frame.addBlock(Helpers.localDateToBytes(date));
        dm.send(frame);

        List<byte[]> resp = dm.receive(Frame.BASIC).getData();
        if(!resp.isEmpty()){
            trataErros(new String(resp.get(0),StandardCharsets.UTF_8));
        }
    }

    public void cancelaBooking(String bookingId) throws IOException, FlightNotFoundException, DayClosedException, AccountException, WrongCredentials, UnknownError, BookingNotFound, FlightFullException, MaxFlightsException, IncompatibleFlightsException, InterruptedException {
        Frame frame = new Frame(Frame.CANCEL);
        frame.addBlock(bookingId.getBytes(StandardCharsets.UTF_8));
        dm.send(frame);
        Frame resp=dm.receive(Frame.BASIC);
        String msg=new String(resp.getDataAt(0),StandardCharsets.UTF_8);
        trataErros(msg);
    }

    public String getLoggedUser(){
        return logged;
    }

    public void trataErros(String erro) throws AccountException, BookingNotFound, DayClosedException, FlightNotFoundException, FlightFullException, WrongCredentials, UnknownError, MaxFlightsException, IncompatibleFlightsException {
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
                throw new WrongCredentials();
            case "M":
                throw new MaxFlightsException();
            case "I":
                throw new IncompatibleFlightsException();
            case "?":
                throw new UnknownError();
            default:

        }
    }

    public void close(){
        try{
            dm.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
