import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable{

    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag,byte[] data){
            this.tag=tag;this.data=data;
        }
    }

    private ReentrantLock readLock;
    private ReentrantLock writeLock;
    private DataInputStream input;
    private DataOutputStream output;



    public Connection(Socket socket) throws IOException {
        input=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        writeLock.lock();
        output.write(frame.tag);
        output.writeInt(frame.data.length);
        output.write(frame.data);
        output.flush();
        writeLock.unlock();
    }
    public void send(int tag, byte[] data) throws IOException {
        writeLock.lock();
        output.writeInt(tag);
        output.writeInt(data.length);
        output.write(data);
        output.flush();
        writeLock.unlock();
    }

    public Frame receive() throws IOException {
        readLock.lock();
        int tag=input.readInt();
        int size=input.readInt();
        byte[] data=new byte[size];
        input.readFully(data);
        readLock.unlock();
        return new Frame(tag,data);
    }

    @Override
    public void close() throws Exception {

    }
}
