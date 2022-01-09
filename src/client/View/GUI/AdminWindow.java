package client.View.GUI;

import client.Controller.Controller;

import java.awt.*;
import java.util.ArrayList;

public class AdminWindow extends Window{
    public AdminWindow(Controller controller, Dimension dimension){
        super("Flight Booking",true,dimension,controller);
        createComponents();
    }
    private void createComponents(){
        ArrayList<DupleCompPos> components;
        components= new ArrayList<>();
        //components.add(new DupleCompPos(buttons(0),BorderLayout.PAGE_START));
        components.add(new DupleCompPos(others(),BorderLayout.PAGE_END));
        setComponents(components);
    }
}
