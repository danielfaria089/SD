package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Login implements ActionListener {

    JFrame frame;
    JTextField username;
    JPasswordField password;

    public Login(){
        frame=new JFrame();

        frame.add(this.credentials(),BorderLayout.CENTER);
        frame.add(this.others(),BorderLayout.PAGE_END);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("ADONDEBOU v2");
    }

    public void showLogin(){
        frame.pack();
        frame.setSize(500,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel credentials(){
        JPanel panel=new JPanel();

        username=new JTextField(20);
        password=new JPasswordField(20);
        JLabel usernameLabel=new JLabel("Username");
        JLabel passwordLabel=new JLabel("Password");

        username.setActionCommand("Username");
        username.addActionListener(this);
        password.setActionCommand("Password");
        password.addActionListener(this);

        username.setAlignmentX(Box.LEFT_ALIGNMENT);
        password.setAlignmentX(Box.LEFT_ALIGNMENT);



        panel.setBorder(BorderFactory.createEmptyBorder(30,10,10,10));
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(passwordLabel);
        panel.add(password);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        return panel;
    }

    private JPanel others(){
        JPanel panel=new JPanel();

        JButton send=new JButton("Login");

        send.setActionCommand("Send");
        send.addActionListener(this);
        send.setAlignmentX(Box.CENTER_ALIGNMENT);
        send.setAlignmentY(Box.BOTTOM_ALIGNMENT);

        panel.setBorder(BorderFactory.createEmptyBorder(30,10,10,10));
        panel.add(send);
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        return panel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command=e.getActionCommand();
        if(command.equals("Send")){
            System.out.println(username.getText()+" | "+ Arrays.toString(password.getPassword()));
        }
    }
}
