package server;

import common.Exceptions.FlightException;
import common.Flight;
import common.Frame;

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

    public List<Frame> createFlightsFrame() throws IOException {
        List<Frame> frames = new ArrayList<>();
        for(Flight f : defaultFlights.values()){
            Frame frame = new Frame(Frame.FLIGHT);
            frame.addBlock(f.createFrame().serialize());
            frames.add(frame);
        }
        return frames;
    }

    public void addDefaultFlight(Flight flight) throws FlightException {
        if(defaultFlights.containsKey(flight.getId()))throw new FlightException();
    }
}
