package client.View.GUI;

import client.Controller.Controller;
import common.Exceptions.*;
import common.Flight;
import common.Helpers;
import common.Pair;
import common.StopOvers;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientWindow extends Window{

    public ClientWindow(Controller controller, Dimension dimension){
        super("Flight Booking",true,dimension,controller);
        createComponents(0);
    }

    private void createComponents(int t){
        ArrayList<DupleCompPos> components;
        components= new ArrayList<>();
        components.add(new DupleCompPos(buttons(t),BorderLayout.PAGE_START));
        components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
        setComponents(components);
    }

    private JPanel buttons(int selected){
        JPanel panel=new JPanel();


        JButton reservation=new JButton("Book a Flight");
        JButton flights=new JButton("Available Flights");
        JButton cancelation=new JButton("Cancel a Booking");


        switch (selected) {
            case 1:
                reservation.setBorder(new LineBorder(Color.BLACK));
                break;
            case 3:
                flights.setBorder(new LineBorder(Color.BLACK));
                break;
            case 4:
                cancelation.setBorder(new LineBorder(Color.BLACK));
                break;
        }

        reservation.addActionListener(e -> {
            Window flight = new FlighRegisterWindow(getController(),getFrame().getSize());
            flight.show();
        });

        flights.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(3),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(flights(),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
        });
        cancelation.addActionListener(e->{
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(4),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(cancelation(),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
        });
        panel.add(reservation);
        panel.add(cancelation);
        panel.add(flights);

        panel.setLayout(new GridLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel bookings(String city1,String city2,LocalDate date1,LocalDate date2){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());
        try{
            List<Pair<LocalDate,StopOvers>>list=getController().getPossibleBookings(city1, city2, date1, date2);
            if(list.isEmpty()){
                popupMessage("There are no available flights",WARNING);
            }
            String[] array=new String[list.size()];
            int count=0;
            for(Pair<LocalDate,StopOvers> elem:list){
                array[count]=elem.fst.toString()+":"+elem.snd.toString();
                count++;
            }
            JList<String> jlist=new JList<>(array);
            jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jlist.setLayoutOrientation(JList.VERTICAL);
            JScrollPane pane=new JScrollPane(jlist);

            JButton book=new JButton("Book Selected Flights");
            book.addActionListener(e->{
                int selected=jlist.getSelectedIndex();
                if(selected>=0) {
                    Pair<LocalDate,StopOvers> pair=list.get(selected);
                    try {
                        String result=getController().reservation(pair.snd,pair.fst);
                        popupMessage("ID de Reserva: "+result,SUCCESS);
                    }catch (FlightFullException ffe){
                        popupMessage("Flight is already full",ERROR);
                    }
                    catch (Exception exception) {
                        popupMessage("INTERNAL ERROR:"+exception.getMessage(),ERROR);
                    }
                }
                else {
                    popupMessage("Please select a booking",WARNING);
                }
            });
            JPanel aux=new JPanel();
            FlowLayout layout=new FlowLayout();
            layout.setAlignment(FlowLayout.RIGHT);
            aux.setLayout(layout);
            aux.add(book);

            panel.add(aux,BorderLayout.PAGE_END);
            panel.add(pane,BorderLayout.CENTER);
        } catch (IOException | InterruptedException e) {
            popupMessage("INTERNAL ERROR:\n"+e.getMessage(),ERROR);
        } catch (DateTimeException dte){
            popupMessage("Invalid Time Span",ERROR);
        }
        return panel;
    }

    private JPanel cancelation(){
        JPanel panel=new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();

        try{
            JComboBox<String> ids=new JComboBox<>(getController().getBookings());
            JButton button=new JButton("Cancel Booking");
            button.addActionListener(e->{
                try{
                    getController().cancelBooking((String) ids.getSelectedItem());
                    ids.removeItemAt(ids.getSelectedIndex());
                    popupMessage("Booking Canceled",SUCCESS);
                }catch (Exception exception) {
                    popupMessage("INTERNAL ERROR:"+exception.getMessage(),ERROR);
                }
            });
            addToGridBag(panel,ids,c,0,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,button,c,0,1,1,1,new Insets(5,0,0,0),GridBagConstraints.WEST);

        } catch (IOException | InterruptedException e) {
            popupMessage("INTERNAL ERROR:"+e.getMessage(),ERROR);
        }

        return panel;
    }

    private JScrollPane flights(){
        try{
            List<Flight> flights=getController().getAllFlights();
            String[][] data=new String[flights.size()][3];
            int i=0;
            for(Flight f:flights){
                data[i][0]=f.getOrigin();
                data[i][1]=f.getDestination();
                data[i][2]=Integer.toString(f.getCapacity());
                i++;
            }
            JTable table=new JTable(data,new String[]{"Origin","Destination","Capacity"});
            return new JScrollPane(table);

        } catch (Exception e) {
            popupMessage("INTERNAL ERROR:"+e.getMessage(),ERROR);
            return new JScrollPane();
        }
    }
}