package client.Controller;

import client.Model.ClientConnection;
import common.Exceptions.*;
import common.Exceptions.UnknownError;
import common.Pair;
import common.StopOvers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private ClientConnection connection;

    public Controller(ClientConnection connection){
        this.connection=connection;
    }

    public String[] login(String username,char[]password) throws IOException, WrongFrameTypeException {
        return connection.login(username,password);
    }

    public void cancelDay(LocalDate date){
       connection.cancelaDia(date);
    }

    public void adicionaDefaultFlight(String origem, String destino, String capacidade) throws IOException {
        connection.adicionaDefaultFlight(origem,destino,capacidade);
    }

    public String[] getCityNames() throws IOException {
        return connection.getCities().toArray(new String[0]);
    }

    public List<Pair<LocalDate,StopOvers>> getPossibleBookings(String orig, String dest, LocalDate dateBegin, LocalDate dateEnd) throws IOException {
        List<Pair<LocalDate,StopOvers>> list=connection.getPossibleBookings(orig, dest, dateBegin, dateEnd);
        return list;
    }

    public String specificReservation(List<String> stopOvers,LocalDate dateBegin,LocalDate dateEnd){

    }

    public String reservation(StopOvers stopOvers,LocalDate date) throws IOException, BookingNotFound, FlightNotFoundException, WrongCredentials, FlightFullException, WrongFrameTypeException, DayClosedException, AccountException, UnknownError {
        return connection.reservation(stopOvers,date);
    }
}
