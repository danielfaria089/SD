package client.View.GUI;

import client.Controller.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ClientWindow extends Window{

    public ClientWindow(Controller controller, Dimension dimension){
        super("Flight Booking",true,dimension,controller);
        createComponents();
    }

    private void createComponents(){
        ArrayList<DupleCompPos> components;
        components= new ArrayList<>();
        components.add(new DupleCompPos(buttons(0),BorderLayout.PAGE_START));
        components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
        setComponents(components);
    }

    private JPanel buttons(int selected){
        JPanel panel=new JPanel();


        JButton reservation=new JButton("Book a Flight");
        JButton stopOvers=new JButton("Book Specific Route");
        JButton flights=new JButton("Available Flights");
        JButton cancelation=new JButton("Cancel a Flight");


        switch (selected) {
            case 1:
                reservation.setBorder(new LineBorder(Color.BLACK));
                break;
            case 2:
                flights.setBorder(new LineBorder(Color.BLACK));
                break;
            case 3:
                cancelation.setBorder(new LineBorder(Color.BLACK));
                break;
        }

        reservation.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(1),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(reservation(),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
        });
        reservation.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(2),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(reservation(),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
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
        panel.add(stopOvers);
        panel.add(cancelation);
        panel.add(flights);

        panel.setLayout(new GridLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel reservation(){
        JPanel panel=new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();

        String[]arrayCities=getController().getCityNames();

        JComboBox<String> cities1=new JComboBox<>(arrayCities);
        cities1.setEditable(true);
        JComboBox<String> cities2=new JComboBox<>(arrayCities);
        cities2.setEditable(true);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        DateFormatter df = new DateFormatter(format);
        JFormattedTextField datefield1 = new JFormattedTextField(df);
        JFormattedTextField datefield2 = new JFormattedTextField(df);

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
            if(cities1.getSelectedItem()!=null&&cities2.getSelectedItem()!=null){
                if(datefield1.getText().length()==10&&datefield2.getText().length()==10){
                    System.out.println(cities1.getSelectedItem()+((String)cities2.getSelectedItem())+datefield1.getText()+datefield2.getText());
                }
                else popupMessage("Insert date range",WARNING);
            }
        });

        addToGridBag(panel,cities1,c,0,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
        addToGridBag(panel,cities2,c,1,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
        addToGridBag(panel,datefield1,c,2,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);
        addToGridBag(panel,datefield2,c,3,0,1,1,new Insets(0,0,0,0),GridBagConstraints.CENTER);

        addToGridBag(panel,new JLabel("   "),c,0,1,3,1,new Insets(10,0,0,0),GridBagConstraints.CENTER);
        addToGridBag(panel,confirm,c,2,1,1,1,new Insets(10,0,0,0),GridBagConstraints.CENTER);

        panel.setBorder(borderCreator("Flight Booking"));
        return panel;
    }

    private JPanel stopOvers(){
        JPanel panel=new JPanel();


        return panel;
    }

    private JPanel cancelation(){
        JPanel panel=new JPanel();


        return panel;
    }

    private JPanel flights(){
        JPanel panel=new JPanel();


        return panel;
    }
}