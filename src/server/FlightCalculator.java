package server;

import common.Exceptions.FlightException;
import common.Exceptions.IncompatibleFlightsException;
import common.Exceptions.MaxFlightsException;
import common.Flight;
import common.StopOvers;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class FlightCalculator {
    private Map<String, Flight> defaultFlights;
    private Map<String, Set<String>> adjacencies;

    private ReadWriteLock l = new ReentrantReadWriteLock();
    private Lock l_r = l.readLock();
    public Lock l_w = l.writeLock();

    public FlightCalculator(){
        defaultFlights = new TreeMap<>();
        adjacencies = new TreeMap<>();
    }

    public FlightCalculator(String filename) throws IOException {
        defaultFlights = new HashMap<>();
        adjacencies = new HashMap<>();
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

    public void writeFlights(String filename) throws IOException {
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
        l_r.lock();
        try {
            Set<String> keySet=adjacencies.keySet();
            Set<String> set=new TreeSet<>(keySet);
            try{
                for(Set<String> adj: adjacencies.values()){
                    set.addAll(adj);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return set;
        }finally {
            l_r.unlock();
        }
    }

    public Set<Flight> getDefaultFlights(){
        l_r.lock();
       try {
           return defaultFlights.values().stream().map(Flight::clone).collect(Collectors.toSet());
       }finally {
           l_r.unlock();
       }
    }

    public Flight getDefaultFlight(String id){
        return defaultFlights.get(id);
    }

    public void addDefaultFlight(Flight flight) throws FlightException {
        if(defaultFlights.containsKey(flight.getId()))throw new FlightException();
        else{
            defaultFlights.put(flight.getId(),flight.clone());
            addAdjencency(flight.getOrigin(),flight.getDestination());
        }
    }

    public List<StopOvers> getFlights(String origin, String destination){
        l_r.lock();
        try{
            List<StopOvers> stopOversList = new ArrayList<>();
            List<List<String>> ways = new ArrayList<>();
            List<String> current = new ArrayList<>();
            depthFirst(origin, destination, ways, current);
            for (List<String> strings : ways) {
                StopOvers stopOvers = new StopOvers();
                try {
                    for (int i = 0; i < strings.size() - 1; i++) {
                        String o = strings.get(i);
                        String d = strings.get(i + 1);
                        Flight f = getFlight(o, d);
                        if (f != null) stopOvers.addFlight(f);
                    }
                    stopOversList.add(stopOvers);
                } catch (IncompatibleFlightsException | MaxFlightsException e) {
                    e.printStackTrace();
                }
            }
            return stopOversList;
        }finally {
            l_r.unlock();
        }
    }

    private void depthFirst(String origin,String destination,List<List<String>> added,List<String>current){
        if(current.size()>Flights.MAX_FLIGHTS)return;
        current.add(origin);
        Set<String> adj = adjacencies.get(origin);
        if(adj != null) {
            for (String string : adj) {
                List<String> newCurrent = new ArrayList<>(current);
                if (string.equals(destination)) {
                    newCurrent.add(string);
                    added.add(newCurrent);
                } else depthFirst(string, destination, added, newCurrent);
            }
        }
    }

    private Flight getFlight(String o,String d){
        for(Flight f:defaultFlights.values()){
            if(f.equals(o,d))return f;
        }
        return null;
    }
}
