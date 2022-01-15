package client.View.GUI;

import client.Controller.Controller;
import common.Exceptions.AccountException;
import common.Exceptions.WrongFrameTypeException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.AccessException;
import java.util.ArrayList;

public class LoginWindow extends Window{

    private JTextField username;
    private JPasswordField password;
    private Dimension otherWindowsSize;

    public LoginWindow(Controller controller){
        super("Autentication",true,new Dimension(300,400),controller);
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
        JLabel usernameLabel=new JLabel("Username:");
        JLabel passwordLabel=new JLabel("Password:");

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

        JButton login=new JButton("Log In");
        JButton cancel=new JButton("Cancel");
        JButton register= new JButton("Register");

        login.addActionListener(e->{
            try {
                String[] result = getController().login(username.getText(),password.getPassword()); // result tem os id's das reservas canceladas a partir do indice 1
                int id = Integer.parseInt(result[0]);
                if(id>0){
                    popupMessage("Logged IN",SUCCESS);
                    setBase(false);
                    Window window;

                    if(id==1){
                        window=new ClientWindow(getController(),otherWindowsSize);
                        if(result.length > 1){
                            StringBuilder sb = new StringBuilder();
                            sb.append("The following reservations have been cancelled: ").append("\n");
                            for(int i = 1; i < result.length ; i++)
                                sb.append("\t-").append(result[i]).append("\n");
                            popupMessage(sb.toString(),WARNING);
                        }
                    }
                    else
                        window=new AdminWindow(getController(),otherWindowsSize);

                    window.show();
                    this.close(false);
                }
                else{
                    popupMessage("Wrong Credentials",ERROR);
                    username.setText("");
                    password.setText("");
                }
            } catch (IOException | WrongFrameTypeException | InterruptedException ioException) {
                popupMessage("Internal Error",ERROR);
            }
        });

        register.addActionListener(e->{
            try {
                getController().register(username.getText(),password.getPassword());
                popupMessage("Account Created",SUCCESS);
                setBase(false);
                Window window=new ClientWindow(getController(),otherWindowsSize);
                window.show();
                this.close(false);
            }catch (AccountException ae){
                popupMessage("Username already exists",WARNING);
            }catch (Exception ex){
                popupMessage("Internal Error",ERROR);
            }

        });

        cancel.addActionListener(e->this.close(true));
        cancel.setBackground(Color.RED);
        cancel.setForeground(Color.WHITE);

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(login);
        panel.add(register);
        panel.add(cancel);

        return panel;
    }
}
