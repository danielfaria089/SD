package View.GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Window {

    protected static final String WARNING="AVISO";
    protected static final String SUCCESS="SUCCESSO";
    protected static final String ERROR="ERRO";
    protected static final String INFO="INFO";
    protected static final String OTHER="MESSAGE";

    private Controller controller;

    private JFrame frame;
    private boolean base;

    public Window(String title, boolean base, Dimension size, Controller controller){
        this.base=base;
        this.controller=controller;

        frame=new JFrame();
        frame.setSize(size);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(true);
            }
        });
        frame.setTitle(title);
    }

    protected void setComponents(ArrayList<DupleCompPos> components){
        frame.getContentPane().removeAll();
        for(DupleCompPos duple:components){
            frame.add(duple.getFirst(),duple.getSecond());
        }
        frame.invalidate();
        frame.validate();
    }

    protected JFrame getFrame(){
        return frame;
    }

    protected Controller getController(){
        return controller;
    }

    protected void setBase(boolean base){
        this.base=base;
    }



    public void show(){
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public boolean close(boolean confirm){
        if(!confirm){
            frame.dispose();
            if(base){
                System.exit(0);
            }
            else{
                return true;
            }
        }
        else{
            int result;
            if(base)result = JOptionPane.showConfirmDialog(frame,"Tem a certeza que quer fechar a aplicação","Encerrar",JOptionPane.YES_NO_OPTION);
            else result = JOptionPane.showConfirmDialog(frame,"Tem a certeza que quer fechar esta janela?","Fechar",JOptionPane.YES_NO_OPTION);
            if(result==JOptionPane.YES_OPTION)return close(false);
            else return false;
        }
        return false;
    }

    protected Border borderCreator(String title){
        Border border=BorderFactory.createLineBorder(Color.BLACK);
        return BorderFactory.createTitledBorder(border,title, TitledBorder.LEADING,TitledBorder.ABOVE_TOP);
    }

    protected void addToGridBag(JPanel panel,Component comp,GridBagConstraints c,int x,int y,int width,int height,Insets pad,int anchor){
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.insets=pad;
        c.anchor=anchor;
        panel.add(comp,c);
    }

    protected void popupMessage(String message,String messageType){
        int type;
        switch (messageType){
            case ERROR:
                type=JOptionPane.ERROR_MESSAGE;
                break;
            case SUCCESS:
                type=JOptionPane.PLAIN_MESSAGE;
                break;
            case INFO:
                type=JOptionPane.INFORMATION_MESSAGE;
                break;
            case WARNING:
                type=JOptionPane.WARNING_MESSAGE;
                break;
            default:
                type=JOptionPane.PLAIN_MESSAGE;
                messageType=OTHER;
                break;
        }
        JOptionPane.showMessageDialog(frame,message,messageType,type);
    }

    public PlainDocument maxLength(int max, JTextArea descricao){
        return new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null || descricao.getText().length() >= max) {
                    return;
                }
                super.insertString(offs, str, a);
            }
        };
    }

    protected JPanel others(){
        JPanel panel=new JPanel(new BorderLayout());
        JPanel panel1=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panel2=new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel label = new JLabel(getController().getLoggado()+" está autenticado");
        panel1.add(label);

        JButton logout=new JButton("Logout");
        logout.addActionListener(e -> {
            LoginWindow loginWindow=new LoginWindow(getController());
            setBase(false);
            if(close(true))loginWindow.show();
            else setBase(true);
        });

        JButton cancel=new JButton("Cancelar");
        cancel.addActionListener(e->this.close(true));
        cancel.setBackground(Color.RED);
        cancel.setForeground(Color.WHITE);

        panel2.add(logout);
        panel2.add(cancel);

        panel.add(panel1,BorderLayout.WEST);
        panel.add(panel2,BorderLayout.EAST);

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

}