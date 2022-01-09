package client.Model;

import common.Frame;
import common.TaggedConnection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Desmultiplexer implements Runnable {
    private final TaggedConnection tc;
    private Lock l;
    private Map<Integer,Entrada> entradas;
    private IOException exception = null;

    public void run() {
        try {
            while(true){
                Frame f = tc.receive();
                l.lock();
                try {
                    Entrada e = getEntrada(f.getType());
                    e.queue.add(f);
                    e.c.signal();
                }finally {
                    l.unlock();
                }
            }
        }catch (IOException e) {
            l.lock();
            try {
                exception = e;
                entradas.forEach((k,v) -> v.c.signalAll());
            }finally {
                l.unlock();
            }
        }
    }

    public Entrada getEntrada(int tag){
        Entrada e;
        l.lock();
        try {
            if (entradas.containsKey(tag)) {
                e = entradas.get(tag);
            } else {
                e = new Entrada(l);
                entradas.put(tag, e);
            }
        }finally {
            l.unlock();
        }
        return e;
    }
    public static class Entrada{
        public ArrayDeque<Frame> queue;
        public Condition c;
        public int waiters;

        Entrada(Lock l){
            queue = new ArrayDeque<>();
            this.c = l.newCondition();
            waiters = 0;
        }
    }


    public Desmultiplexer(TaggedConnection tc){
        this.tc = tc;
        entradas = new HashMap<>();
    }

    public void start(){
        Thread ouve = new Thread(this);
        ouve.start();
    }
    public void send(Frame frame) throws IOException {
        tc.send(frame);
    }
    public void send(byte tag, List<byte[]> data) throws IOException {
        tc.send(tag,data);
    }
    public Frame receive(int tag) throws IOException, InterruptedException {
        l.lock();

        try {
            Entrada e = getEntrada(tag);
            e.waiters++;
            for (; ; ) {
                if (!e.queue.isEmpty()) {
                    Frame res = e.queue.poll();
                    e.waiters--;
                    if (e.queue.isEmpty() && e.waiters == 0)
                        entradas.remove(tag);
                    return res;
                }
                if (exception != null)
                    throw exception;
                e.c.await();
            }
        }finally {
            l.unlock();
        }
    }
    public void close() throws IOException {
        tc.close();
    }
}