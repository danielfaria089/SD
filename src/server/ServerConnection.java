package server;

import common.Credentials;
import common.Exceptions.*;
import common.Frame;
import common.*;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection implements Runnable, AutoCloseable{
    private TaggedConnection tc;
    private DataBase dataBase;

    private String loggedUser;

    public ServerConnection(Socket socket,DataBase dataBase) throws IOException {
        this.tc = new TaggedConnection(socket);
        this.dataBase=dataBase;
    }

    public boolean receive() throws IOException, WrongFrameTypeException{
        Frame frame=tc.receive();
        if(frame == null) return false;

        try {
            switch (frame.getType()) {
                case Frame.LOGIN:
                    login(frame);//Recebe credentials e dá login
                    break;
                case Frame.BOOKING:
                    reservation(frame);//Recebe uma trip e regista os voos
                    break;
                case Frame.ALL_FLIGHTS:
                    allFlights();//Recebe uma trip e regista os voos
                    break;
                case Frame.ACCOUNT_FLIGHTS:
                    getAllBookingsFromAccount();
                    break;
                case Frame.CITIES:
                    sendCities();//Envia a lista de cidades
                    break;
                case Frame.STOPOVERS:
                    sendPossibleBookings(frame);
                    break;
                case Frame.NOTIF:
                    getNotificacoes(frame); // envia notificacoes de cancelamento
                    break;
                case Frame.CANCEL:
                    cancelaBooking(frame);
                    break; // cancela um booking
                case Frame.CANCEL_DAY:
                    cancelaDia(frame);
                    break;
                case Frame.FLIGHT:
                    adicionaDefaultFlight(frame);
                    break;
                case Frame.SPEC_BOOK:
                    specificBooking(frame);// regista um booking com percurso especifico
                    break;
                default:
                    return false;
            }
        }catch (IOException | WrongFrameTypeException e){
            throw e;
        }catch (Exception e){
            Frame failure=new Frame(Frame.BASIC);
            trataErros(e,failure);
            tc.send(failure);
        }
        return true;
    }

    private void login(Frame frame) throws IOException, WrongFrameTypeException, AccountException, WrongCredentials {
        Credentials credentials=new Credentials(frame);
        if(dataBase.checkLogIn(credentials.getUsername(), credentials.getPassword())){
            loggedUser=credentials.getUsername();
            Frame success=new Frame(Frame.BASIC);
            if(dataBase.checkAdmin(loggedUser)){
                success.addBlock("ADMIN".getBytes(StandardCharsets.UTF_8));
            }
            else{
                success.addBlock("CLIENT".getBytes(StandardCharsets.UTF_8));
                Set<String> notif = dataBase.getNotificacoesCliente(loggedUser);
                for(String not : notif)
                    success.addBlock(not.getBytes(StandardCharsets.UTF_8));
            }

            tc.send(success);
        }
        else{
            throw new WrongCredentials();
        }
    }

    public void sendPossibleBookings(Frame frame) throws IOException {
        String origin=new String(frame.getDataAt(0),StandardCharsets.UTF_8);
        String destination=new String(frame.getDataAt(1),StandardCharsets.UTF_8);

        LocalDate date1=Helpers.localDateFromBytes(frame.getDataAt(2));
        LocalDate date2=Helpers.localDateFromBytes(frame.getDataAt(3));
        Set<Booking> bookings=dataBase.possibleBookings(origin,destination,date1,date2);

        Frame sentFrame=new Frame(Frame.STOPOVERS);

        for(Booking book:bookings){
            sentFrame.addBlock(Helpers.localDateToBytes(book.getDate()));
            sentFrame.addBlock(book.getStopOvers().createFrame().serialize());
        }

        tc.send(sentFrame);
    }

    //Frame que recebe: (byte)Type :(0) (LocalDate)data -> (1) (Trip)viagem
    private void reservation(Frame frame) throws IOException, WrongFrameTypeException{
        LocalDate date=Helpers.localDateFromBytes(frame.getDataAt(0));
        StopOvers stopOvers=new StopOvers(new Frame(frame.getDataAt(1)));

        Booking booking=new Booking(loggedUser,date,stopOvers);

        Frame success=new Frame(Frame.BASIC);
        try{
            dataBase.addBooking(booking);
            success.addBlock(booking.getBookingID().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e){
            trataErros(e,success);
        }
        tc.send(success);
    }


    private void allFlights() throws IOException {
        Frame frame=new Frame(Frame.ALL_FLIGHTS);
        Set<Flight> flights = dataBase.getDefaultFlights();
        for(Flight f : flights){
            frame.addBlock(f.createFrame().serialize());
        }
        tc.send(frame);
    }

    public void sendCities()throws IOException{
        Frame frame=new Frame(Frame.CITIES);
        Set<String>cities=dataBase.getAllCities();
        for(String city:cities){
            frame.addBlock(city.getBytes(StandardCharsets.UTF_8));
        }
        tc.send(frame);
    }

    public void getAllBookingsFromAccount() throws IOException {
        Frame frame = new Frame(Frame.ACCOUNT_FLIGHTS);
        Set<String> set = dataBase.getAccount(loggedUser).getBookingsIds();
        for(String s : set){
            frame.addBlock(s.getBytes(StandardCharsets.UTF_8));
        }
        tc.send(frame);
    }

    public void getNotificacoes(Frame frame) throws IOException, AccountException {
        Frame success=new Frame(Frame.NOTIF,new ArrayList<>());
        if (!dataBase.checkAdmin(loggedUser)) {
            Set<String> notif = dataBase.getNotificacoesCliente(loggedUser);
            for (String not : notif)
                success.addBlock(not.getBytes(StandardCharsets.UTF_8));
        }
        tc.send(success);
    }

    public void cancelaBooking(Frame frame) throws IOException, DayClosedException, AccountException, BookingNotFound, WrongFrameTypeException {
        Frame status = new Frame(Frame.BASIC,new ArrayList<>());
        List<byte[]> resp = frame.getData();
        if(resp.size() == 1){
            dataBase.cancelBooking(loggedUser,new String(resp.get(0),StandardCharsets.UTF_8));
            tc.send(status);
        }else{
            throw new WrongFrameTypeException();
        }
    }

    public void cancelaDia(Frame frame) throws IOException, WrongFrameTypeException {
        Frame status = new Frame(Frame.BASIC);
        List<byte[]> resp = frame.getData();
        if(resp.size() == 1){
            dataBase.cancelDay(Helpers.localDateFromBytes(resp.get(0)));
            tc.send(status);
        }else{
            throw new WrongFrameTypeException();
        }
    }

    public void adicionaDefaultFlight(Frame frame) throws FlightException, IOException, WrongFrameTypeException {
        Frame status = new Frame(Frame.BASIC);
        List<byte[]> resp = frame.getData();
        if (resp.size() == 3) {
            try {
                dataBase.addDefaultFlight(new Flight(Helpers.randomString(), new String(resp.get(0), StandardCharsets.UTF_8), new String(resp.get(1), StandardCharsets.UTF_8), Helpers.intFromByteArray(resp.get(2))));
                status.addBlock("Success".getBytes(StandardCharsets.UTF_8));
            } catch (Exception fe) {
                trataErros(fe, status);
            }
            tc.send(status);
        } else {
            throw new WrongFrameTypeException();
        }
    }

    public void specificBooking(Frame frame) throws IOException {
        List<byte[]>data=frame.getData();
        List<String> strings=new ArrayList<>();
        LocalDate date1=Helpers.localDateFromBytes(data.get(0));
        LocalDate date2=Helpers.localDateFromBytes(data.get(1));
        for(int i=2;i<data.size();i++){
            strings.add(new String(data.get(i),StandardCharsets.UTF_8));
        }
        Frame success=new Frame(Frame.BASIC);
        try{
            String result=dataBase.registerBooking(loggedUser,strings,date1,date2);
            success.addBlock(result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e){
            trataErros(e,success);
        }
        tc.send(success);
    }

    public void trataErros(Exception e, Frame status){
        if(e instanceof DayClosedException){
            status.addBlock("D".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof BookingNotFound){
            status.addBlock("B".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof AccountException){
            status.addBlock("A".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof FlightNotFoundException){
            status.addBlock("Fn".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof FlightFullException){
            status.addBlock("Ff".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof WrongCredentials) {
            status.addBlock("L".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof FlightException){
            status.addBlock("F".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof IncompatibleFlightsException){
            status.addBlock("I".getBytes(StandardCharsets.UTF_8));
        }else if(e instanceof MaxFlightsException){
            status.addBlock("M".getBytes(StandardCharsets.UTF_8));
        }
         else {
             status.addBlock("?".getBytes(StandardCharsets.UTF_8)); // erro desconhecido
        }
    }

    @Override
    public void run(){
        boolean run=true;
        try {
            while(run) {
                if (!receive()) run=false;
            }
        }catch (EOFException eof){
            //nada
        }
        catch (IOException | WrongFrameTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        tc.close();
    }
}
