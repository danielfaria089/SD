package client.View.GUI;

import client.Controller.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class ClientWindow extends Window{

    private JLabel selected;
    private boolean showAll;

    private JTextArea descricao;

    public ClientWindow(Controller controller, Dimension dimension){
        super("Repair Store",true,dimension,controller);
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


        JButton account=new JButton("My Account");
        JButton reservation=new JButton("Book a Flight");
        JButton flights=new JButton("Available Flights");
        JButton cancelation=new JButton("Cancel a Flight");


        switch (selected) {
            case 1:
                account.setBorder(new LineBorder(Color.BLACK));
                break;
            case 2:
                reservation.setBorder(new LineBorder(Color.BLACK));
                break;
            case 3:
                flights.setBorder(new LineBorder(Color.BLACK));
                break;
            case 4:
                cancelation.setBorder(new LineBorder(Color.BLACK));
                break;
        }

        account.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(1),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(account(),BorderLayout.CENTER));
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

        panel.add(account);
        panel.add(reservation);
        panel.add(flights);
        panel.add(cancelation);
        panel.setLayout(new GridLayout(1,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel account(){
        JPanel panel=new JPanel();
        
        
        return panel;
    }

    private JPanel reservation(){
        JPanel panel=new JPanel();


        return panel;
    }

    private JPanel flights(){
        JPanel panel=new JPanel();


        return panel;
    }

    private JPanel cancelation(){
        JPanel panel=new JPanel();


        return panel;
    }
}
