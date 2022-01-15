package client.View.GUI;

import client.Controller.Controller;
import common.Exceptions.*;
import common.Exceptions.UnknownError;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdminWindow extends Window{
    public AdminWindow(Controller controller, Dimension dimension){
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

        JButton reservation=new JButton("Add New Default Flight");
        JButton cancelation=new JButton("Close Day");

        switch (selected) {
            case 1:
                reservation.setBorder(new LineBorder(Color.BLACK));
                break;
            case 2:
                cancelation.setBorder(new LineBorder(Color.BLACK));
                break;
        }

        reservation.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(1),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(reservation(null),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
        });

        cancelation.addActionListener(e -> {
            ArrayList<DupleCompPos> components;
            components= new ArrayList<>();
            components.add(new DupleCompPos(buttons(2),BorderLayout.PAGE_START));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.EAST));
            components.add(new DupleCompPos(new JPanel(),BorderLayout.WEST));
            components.add(new DupleCompPos(cancelation(null),BorderLayout.CENTER));
            components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
            setComponents(components);
        });

        panel.add(reservation);
        panel.add(cancelation);

        panel.setLayout(new GridLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel reservation(JPanel bookings){
        JPanel panel=new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        JTextField origem = new JTextField(20);
        JTextField destino = new JTextField(20);
        JTextField capacidade = new JTextField(20);
        JButton confirm=new JButton("Create");
        confirm.addActionListener(e->{
            if(origem.getText().length() > 0 && destino.getText().length() > 0 && capacidade.getText().length() > 0){
                try {
                    Integer.parseInt(capacidade.getText());
                    String str = getController().adicionaDefaultFlight(origem.getText(),destino.getText(),capacidade.getText());
                    popupMessage(str,INFO);
                }catch (NumberFormatException error){
                    popupMessage("Invalid Capacity",WARNING);
                }catch (Exception error2){
                    popupMessage("ERROR: " + error2.getClass().getSimpleName(),ERROR);
                }
            }else{
                popupMessage("There are still empty fields",WARNING);
            }
        });
        JLabel l_origem = new JLabel("Origin: ");
        JLabel l_destino = new JLabel("Destiny: ");
        JLabel l_capacidade = new JLabel("Capacity: ");
        addToGridBag(panel,l_origem,gc, 0,0,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,origem,gc,4 ,0,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,l_destino,gc, 0,4,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,destino,gc, 4,4,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,l_capacidade,gc, 0,6,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,capacidade,gc, 4,6,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,confirm,gc, 5,8,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        return panel;
    }

    private JPanel cancelation(JPanel bookings){
        JPanel panel=new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();

        JLabel label = new JLabel("dd/MM/yyyy : ");

        JTextField datefield = new JTextField(10);
        datefield.setPreferredSize(new Dimension(100,20));

        datefield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent key) {
                char c=key.getKeyChar();
                String text=datefield.getText();
                if(text.length()>=10)key.consume();
                else{
                    if((c >= '0') && (c <= '9') ){
                        if(text.length()==2||text.length()==5)datefield.setText(text+"/");
                    }
                    else if(c == KeyEvent.VK_BACK_SPACE){
                        if(text.length()==3||text.length()==6)datefield.setText(text.substring(0,text.length()-1));
                    }
                    else key.consume();
                }
            }
        });

        JButton confirm=new JButton("Confirm");
        confirm.addActionListener(e->{
                if(datefield.getText().length()==10){
                    String[] data = datefield.getText().split("/");
                    LocalDate date=LocalDate.parse(datefield.getText(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String[] data_r = date.toString().split("-");
                    if(data[0].equals(data_r[2]) && data[1].equals(data_r[1]) && data[2].equals(data_r[0])){
                        try {
                            getController().cancelDay(date);
                            popupMessage("Day Canceled",INFO);
                        } catch (DateTimeException es){
                            popupMessage("Date is before the present day",WARNING);
                        } catch (Exception ex) {
                            popupMessage("ERROR: " + ex.getClass().getSimpleName(),ERROR);
                        }
                    }else{
                        popupMessage("Invalid date",WARNING);
                    }
                }
                else popupMessage("Insert date",WARNING);
        });

        addToGridBag(panel,label,c, 0,0,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,datefield,c, 5,0,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);
        addToGridBag(panel,confirm,c, 5,8,2,2,new Insets(0,0,5,0),GridBagConstraints.EAST);

        return panel;
    }

}
