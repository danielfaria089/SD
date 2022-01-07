package server;

import common.Exceptions.FlightException;
import common.Exceptions.FlightNotFoundException;
import common.Exceptions.IncompatibleFlightsException;
import common.Exceptions.MaxFlightsException;
import common.Flight;
import common.StopOvers;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FlightCalculator {
    private Map<String, Flight> defaultFlights;
    private Map<String, Set<String>> adjacencies;

    public FlightCalculator(String filename) throws IOException {
        readFlights(filename);
    }

    private void readFlights(String filename) throws IOException {
        BufferedReader reader = new BufferedReader((new FileReader(filename)));
        String line;
        while ((line = reader.readLine())!=null){
            String[] strings = line.split(";");
            Flight f = new Flight(strings[0],strings[1],strings[2],Integer.parseInt(strings[3]));
            addAdjencency(f.getOrigin(),f.getDestination());
            defaultFlights.put(f.getId(),f);
        }
    }

    private void writeFlights(String filename) throws IOException {
        PrintWriter writer = new PrintWriter((new FileWriter(filename)));
        for(Flight f : defaultFlights.values()){
            writer.println(f.getId() + ";" +
                    f.getOrigin() + ";" +
                    f.getDestination() + ";" +
                    f.getCapacity());
        }
        writer.flush();
        writer.close();
    }

    private void addAdjencency(String origem,String destino){
        if(!adjacencies.containsKey(origem))adjacencies.put(origem,new TreeSet<>());
        adjacencies.get(origem).add(destino);
    }

    public Set<String> getAllCities(){
        return adjacencies.keySet();
    }

    public Set<Flight> getDefaultFlights(){
        return defaultFlights.values().stream().map(Flight::clone).collect(Collectors.toSet());
    }

    public void addDefaultFlight(Flight flight) throws FlightException {
        if(defaultFlights.containsKey(flight.getId()))throw new FlightException();
    }

    public Set<StopOvers> getFlights(String origin, String destination) throws FlightNotFoundException, IncompatibleFlightsException, MaxFlightsException {
        Set<StopOvers> stopOversSet= new TreeSet<>(StopOvers::compare);
        List<List<String>> ways=new ArrayList<>();
        List<String> current=new ArrayList<>();
        current.add(origin);
        //depthFirst(origin,destination,ways,current);
        for(List<String>strings:ways){
            StopOvers stopOvers=new StopOvers();
            for(int i=0;i< strings.size()-1;i++){
                String o=strings.get(i);
                String d=strings.get(i+1);
                Flight f=getFlight(o,d);
                if(f==null)throw new FlightNotFoundException();
                stopOvers.addFlight(f);
            }
            stopOversSet.add(stopOvers);
        }
        return stopOversSet;
    }

    private void depthFirst(String origin,String destination,List<List<String>> added,List<String>current){
        if(current.size()>Flights.MAX_FLIGHTS)return;
        current.add(origin);
        for(String string: adjacencies.get(origin)){
            List<String> newCurrent=new ArrayList<>(current);
            newCurrent.add(string);
            if(string.equals(destination)){
                added.add(newCurrent);
            }
            else depthFirst(origin, destination, added, newCurrent);
        }
    }

    private Flight getFlight(String o,String d){
        for(Flight f:defaultFlights.values()){
            if(f.equals(o,d))return f;
        }
        return null;
    }
}
