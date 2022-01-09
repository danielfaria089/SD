package client.Model;

import java.net.Socket;
import java.sql.Struct;
import java.util.Arrays;

public class ThreadedClient {
    public static void main(String[] args) throws Exception {

        //Desmultiplexer m = new Desmultiplexer(new TaggedConnection(s));
        //m.start();

        Thread[] threads = {

                new Thread(() -> {
                    try  {
                        ClientConnection cc = new ClientConnection("localhost",12345);
                        String[] i = cc.login("ola",new char[]{'1','2','3'});
                        System.out.println(Arrays.toString(i));
                        System.out.println(Arrays.toString(cc.pedeNotificacoes()));
                    }  catch (Exception ignored) {}
                }),
/*
                new Thread(() -> {
                    try  {
                        ClientConnection cc = new ClientConnection("localhost",12345);
                        String i = cc.login("ola",new char[]{'1','2','3'});
                        System.out.println(i);
                    }  catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        ClientConnection cc = new ClientConnection("localhost",12345);
                        String i = cc.login("ola",new char[]{'1','2','3'});
                        System.out.println(i);
                    }  catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {ClientConnection cc = new ClientConnection("localhost",12345);
                        String i = cc.login("ola",new char[]{'1','2','3'});
                        System.out.println(i);
                    } catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        ClientConnection cc = new ClientConnection("localhost",12345);
                        String i = cc.login("ola",new char[]{'1','2','3'});
                        System.out.println(i);
                    } catch (Exception ignored) {}
                })*/
        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
      //  m.close();
    }
}
