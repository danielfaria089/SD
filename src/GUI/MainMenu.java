package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu implements ActionListener {

    JFrame frame;

    public MainMenu(){
        this.frame=new JFrame();

        frame.add(this.loginButtons(),BorderLayout.CENTER);
        frame.add(this.others(),BorderLayout.PAGE_END);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("ADONDEBOU v2");
    }

    public void showMenu(){
        frame.pack();
        frame.setSize(500,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel loginButtons(){
        JPanel panel=new JPanel();

        JButton login=new JButton("Login");
        JButton loginAdm=new JButton("Login(Admin)");

        login.setActionCommand("Login");
        login.addActionListener(this);
        loginAdm.setActionCommand("Login(Admin)");
        loginAdm.addActionListener(this);

        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginAdm.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.setBorder(BorderFactory.createEmptyBorder(30,10,10,10));
        panel.add(login);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(loginAdm);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        return panel;
    }

    private JPanel others(){
        JPanel panel = new JPanel();
        JButton exit=new JButton("Exit");
        exit.setActionCommand("Exit");
        exit.addActionListener(this);

        panel.setBorder(BorderFactory.createEmptyBorder(30,10,10,10));
        panel.add(Box.createHorizontalGlue());
        panel.add(exit);
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command=e.getActionCommand();
        if(command.equals("Login")){
            Login login=new Login();
            frame.setVisible(false);
            login.showLogin();
        }
        else if(command.equals("Login(Admin)")){
            //abrir login do admin e fechar menu
        }
        else if(command.equals("Exit")){
            frame.dispose();
        }
    }
}
