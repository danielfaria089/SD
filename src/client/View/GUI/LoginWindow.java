package client.View.GUI;

import client.Controller.Controller;
import common.Exceptions.WrongFrameTypeException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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
            try {
                String[] result = getController().login(username.getText(),password.getPassword()); // result tem os id's das reservas canceladas a partir do indice 1
                int id = Integer.parseInt(result[0]);
                if(id>0){
                    popupMessage("Loggado com sucesso",SUCCESS);
                    setBase(false);
                    Window window;

                    if(id==1){
                        window=new ClientWindow(getController(),otherWindowsSize);
                        if(result.length > 1){
                            StringBuilder sb = new StringBuilder();
                            sb.append("As seguintes reservas foram canceladas: ").append("\n");
                            for(int i = 1; i < result.length ; i++)
                                sb.append("\t-").append(result[i]).append("\n");
                            popupMessage(sb.toString(),WARNING);
                        }
                    }
                    else
                        window=new AdminWindow(getController(),otherWindowsSize);

                    window.show();
                }
                else{
                    popupMessage("Credenciais erradas",ERROR);
                    username.setText("");
                    password.setText("");
                }
            } catch (IOException | WrongFrameTypeException ioException) {
                popupMessage("Erro interno",ERROR);
            }
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
