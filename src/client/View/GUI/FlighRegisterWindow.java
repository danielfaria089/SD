package client.View.GUI;

import client.Controller.Controller;
import common.Exceptions.FlightFullException;
import common.Exceptions.FlightNotFoundException;
import common.Helpers;
import common.Pair;
import common.StopOvers;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlighRegisterWindow extends Window{
    public FlighRegisterWindow(Controller controller, Dimension size) {
        super("Flight Registering",true,size,controller);
        createComponents(0);
        setBase(false);
    }

    private void createComponents(int t){
        ArrayList<DupleCompPos> components;
        components= new ArrayList<>();
        components.add(new DupleCompPos(buttons(t),BorderLayout.PAGE_START));
        setComponents(components);
    }

    private JPanel buttons(int selected){
        JPanel panel=new JPanel();


        JButton reservation=new JButton("Book a Flight");
        JButton stopOvers=new JButton("Book Specific Route");


        switch (selected) {
            case 1:
                reservation.setBorder(new LineBorder(Color.BLACK));
                break;
            case 2:
                stopOvers.setBorder(new LineBorder(Color.BLACK));
                break;
        }

        reservation.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(1),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(reservation(null),BorderLayout.CENTER));
            setComponents(components);
        });

        stopOvers.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(2),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(stopOvers(),BorderLayout.CENTER));
            setComponents(components);
        });


        panel.add(reservation);
        panel.add(stopOvers);

        panel.setLayout(new GridLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }
    private JPanel reservation(JPanel bookings){
        JPanel panel=new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();

        try{
            String[]arrayCities=getController().getCityNames();
            JComboBox<String> cities1=new JComboBox<>(arrayCities);
            cities1.setEditable(true);
            JComboBox<String> cities2=new JComboBox<>(arrayCities);
            cities2.setEditable(true);

            JTextField datefield1 = new JTextField();
            JTextField datefield2 = new JTextField();

            datefield1.setPreferredSize(new Dimension(100,20));
            datefield2.setPreferredSize(new Dimension(100,20));

            datefield1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent key) {
                    char c=key.getKeyChar();
                    String text=datefield1.getText();
                    if(text.length()>=10)key.consume();
                    else{
                        if((c >= '0') && (c <= '9') ){
                            if(text.length()==2||text.length()==5)datefield1.setText(text+"/");
                        }
                        else if(c == KeyEvent.VK_BACK_SPACE){
                            if(text.length()==3||text.length()==6)datefield1.setText(text.substring(0,text.length()-1));
                        }
                        else key.consume();
                    }
                }
            });

            datefield2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent key) {
                    char c=key.getKeyChar();
                    String text=datefield2.getText();
                    if(text.length()>=10)key.consume();
                    else{
                        if((c >= '0') && (c <= '9') ){
                            if(text.length()==2||text.length()==5)datefield2.setText(text+"/");
                        }
                        else if(c == KeyEvent.VK_BACK_SPACE){
                            if(text.length()==3||text.length()==6)datefield2.setText(text.substring(0,text.length()-1));
                        }
                        else key.consume();
                    }
                }
            });

            JButton confirm=new JButton("Confirm");
            confirm.addActionListener(e->{
                if(datefield1.getText().length()==10&&datefield2.getText().length()==10){
                    String city1 = (String) cities1.getSelectedItem();
                    String city2 = (String) cities2.getSelectedItem();
                    LocalDate date1 = LocalDate.parse(datefield1.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate date2 = LocalDate.parse(datefield2.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    if(date1.plusMonths(2).isAfter(date2)&& (date1.isBefore(date2) || date1.equals(date2))){
                        if (Helpers.verifyDate(date1, datefield1.getText()) && Helpers.verifyDate(date2, datefield2.getText())) {
                            ArrayList<DupleCompPos> components;
                            components = new ArrayList<>();
                            components.add(new DupleCompPos(buttons(1), BorderLayout.PAGE_START));
                            components.add(new DupleCompPos(new JPanel(), BorderLayout.EAST));
                            components.add(new DupleCompPos(new JPanel(), BorderLayout.WEST));
                            components.add(new DupleCompPos(reservation(bookings(city1, city2, date1, date2)), BorderLayout.CENTER));
                            setComponents(components);
                        } else popupMessage("Invalid date", WARNING);
                    }else popupMessage("Invalid date range", WARNING);
                } else popupMessage("Insert date range",WARNING);
            });

            addToGridBag(panel,new JLabel("Origin"),c,0,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,new JLabel("Destiny"),c,1,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,new JLabel("From:"),c,2,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,new JLabel("To:"),c,3,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);


            addToGridBag(panel,cities1,c,0,1,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,cities2,c,1,1,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,datefield1,c,2,1,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,datefield2,c,3,1,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);

            addToGridBag(panel,new JLabel("   "),c,0,2,3,1,new Insets(10,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(panel,confirm,c,2,3,1,1,new Insets(10,0,0,0),GridBagConstraints.CENTER);

        } catch (IOException | InterruptedException e) {
            popupMessage("INTERNAL ERROR:\n"+e.getMessage(),ERROR);
        }

        if(bookings==null){
            panel.setBorder(borderCreator("Flight Booking"));
            return panel;
        }
        else{
            JPanel aux=new JPanel();
            aux.setLayout(new BorderLayout());
            aux.add(panel,BorderLayout.PAGE_START);
            aux.add(bookings,BorderLayout.CENTER);
            return aux;
        }
    }

    private JPanel bookings(String city1,String city2,LocalDate date1,LocalDate date2){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());
        try{
            java.util.List<Pair<LocalDate, StopOvers>> list=getController().getPossibleBookings(city1, city2, date1, date2);
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

    private JPanel stopOvers(){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());

        try{
            String[]arrayCities=getController().getCityNames();

            DefaultTableModel model=new DefaultTableModel(new String[]{"Stop Overs"},1);
            JTable table=new JTable(model);

            JComboBox<String> comboBox=new JComboBox<>(arrayCities);
            comboBox.setEditable(true);

            table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBox));


            JButton addRow=new JButton("+");
            addRow.addActionListener(e->{
                model.addRow(new String[]{""});
            });
            JButton rmvRow=new JButton("-");
            rmvRow.addActionListener(e->{
                model.removeRow(table.getRowCount()-1);
            });

            JPanel aux1=new JPanel();
            aux1.setLayout(new FlowLayout());
            aux1.add(addRow);
            aux1.add(rmvRow);

            JScrollPane aux2=new JScrollPane(table);

            JTextField datefield1 = new JTextField(10);
            JTextField datefield2 = new JTextField(10);

            datefield1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent key) {
                    char c=key.getKeyChar();
                    String text=datefield1.getText();
                    if(text.length()>=10)key.consume();
                    else{
                        if((c >= '0') && (c <= '9') ){
                            if(text.length()==2||text.length()==5)datefield1.setText(text+"/");
                        }
                        else if(c == KeyEvent.VK_BACK_SPACE){
                            if(text.length()==3||text.length()==6)datefield1.setText(text.substring(0,text.length()-1));
                        }
                        else key.consume();
                    }
                }
            });

            datefield2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent key) {
                    char c=key.getKeyChar();
                    String text=datefield2.getText();
                    if(text.length()>=10)key.consume();
                    else{
                        if((c >= '0') && (c <= '9') ){
                            if(text.length()==2||text.length()==5)datefield2.setText(text+"/");
                        }
                        else if(c == KeyEvent.VK_BACK_SPACE){
                            if(text.length()==3||text.length()==6)datefield2.setText(text.substring(0,text.length()-1));
                        }
                        else key.consume();
                    }
                }
            });

            JButton confirm=new JButton("Confirm");
            confirm.addActionListener(e->{
                if(datefield1.getText().length()==10&&datefield2.getText().length()==10){
                    List<String> stops=new ArrayList<>();
                    for(int i=0;i<table.getRowCount();i++){
                        stops.add((String) table.getValueAt(i,0));
                    }

                    LocalDate date1=LocalDate.parse(datefield1.getText(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate date2=LocalDate.parse(datefield2.getText(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    if(date1.plusMonths(2).isAfter(date2) && (date1.isBefore(date2) || date1.equals(date2))){
                        if(Helpers.verifyDate(date1,datefield1.getText())&&Helpers.verifyDate(date2,datefield2.getText())){
                            try{
                                String result=getController().specificReservation(stops,date1,date2);
                                popupMessage("Booking Successful\nID:"+result,SUCCESS);
                            }catch (FlightNotFoundException exception){
                                popupMessage("Not able to book this trip",ERROR);
                            }catch (DateTimeException dte){
                                popupMessage("Invalid Time Span",ERROR);
                            }
                            catch(Exception exception){
                                popupMessage("INTERNAL ERROR:"+exception.getMessage(),ERROR);
                            }
                        }else popupMessage("Invalid date",WARNING);
                    }else popupMessage("Invalid date range",WARNING);
                }else popupMessage("Insert date range",WARNING);
            });

            JPanel aux3=new JPanel();
            aux3.setLayout(new GridBagLayout());
            GridBagConstraints c=new GridBagConstraints();

            addToGridBag(aux3,new JLabel("From:"),c,0,2,1,1,new Insets(5,0,0,0),GridBagConstraints.WEST);
            addToGridBag(aux3,datefield1,c,1,2,1,1,new Insets(5,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(aux3,new JLabel("To:"),c,2,2,1,1,new Insets(5,0,0,0),GridBagConstraints.WEST);
            addToGridBag(aux3,datefield2,c,3,2,1,1,new Insets(5,0,0,0),GridBagConstraints.CENTER);

            addToGridBag(aux3,new JLabel("         "),c,0,3,4,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
            addToGridBag(aux3,confirm,c,1,4,2,1,new Insets(5,0,0,0),GridBagConstraints.CENTER);


            panel.add(aux1,BorderLayout.PAGE_START);
            panel.add(aux2,BorderLayout.CENTER);
            panel.add(new JPanel(),BorderLayout.WEST);
            panel.add(new JPanel(),BorderLayout.EAST);
            panel.add(aux3,BorderLayout.PAGE_END);
        } catch (IOException | InterruptedException e) {
            popupMessage("INTERNAL ERROR:"+e.getMessage(),ERROR);
        }

        return panel;
    }
}
