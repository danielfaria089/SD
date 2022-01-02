package client.View.GUI;

import client.Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginWindow extends Window{

    private JTextField username;
    private JPasswordField password;
    private Dimension otherWindowsSize;

    public LoginWindow(Controller controller){
        super("Autenticação",true,new Dimension(300,400),controller);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth()/2;
        int height = gd.getDisplayMode().getHeight()/2;
        otherWindowsSize=new Dimension(width,height);
        createComponents();
    }

    private void createComponents(){
        ArrayList<DupleCompPos> components;
        components= new ArrayList<>();
        components.add(new DupleCompPos(credentials(),BorderLayout.PAGE_START));
        components.add(new DupleCompPos(othersLogin(),BorderLayout.PAGE_END));
        setComponents(components);
    }

    private JPanel credentials(){
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        username=new JTextField(20);
        password=new JPasswordField(20);
        JLabel usernameLabel=new JLabel("Utilizador");
        JLabel passwordLabel=new JLabel("Palavra-Passe");

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(passwordLabel);
        panel.add(password);

        return panel;
    }

    private JPanel othersLogin(){
        JPanel panel=new JPanel();
        FlowLayout flowLayout=new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        panel.setLayout(flowLayout);

        JButton login=new JButton("Autenticar");
        JButton cancel=new JButton("Cancelar");

        login.addActionListener(e->{
            getController().login(username.getText(),password.getPassword());
        });
        cancel.addActionListener(e->this.close(true));
        cancel.setBackground(Color.RED);
        cancel.setForeground(Color.WHITE);

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(login);
        panel.add(cancel);

        return panel;
    }
}
