package common;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Frame {
    private byte type;
    private List<byte[]> data;

    public Frame(){
        type=0;
        data=new ArrayList<>();
    }

    public Frame(byte type){
        this.type=type;
        data=new ArrayList<>();
    }

    public Frame(byte type,List<byte[]>data){
        this.type=type;
        this.data=new ArrayList<>();
        for(byte[]block:data){
            this.data.add(Arrays.copyOf(block,block.length));
        }
    }

    public void addBlock(byte[] block){
        data.add(Arrays.copyOf(block,block.length));
    }

    public byte getType(){
        return type;
    }

    public List<byte[]> getData(){
        return data.stream().map(a->Arrays.copyOf(a,a.length)).collect(Collectors.toList());
    }

    public DataInputStream getDataDIS() throws IOException {
        return new DataInputStream(new ByteArrayInputStream(serialize()));
    }

    public static byte[] readBlock(DataInputStream inputStream) throws IOException {
        byte[]buffer=new byte[inputStream.readInt()];
        inputStream.read(buffer,0, buffer.length);
        return buffer;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bao);

        dos.write(type);
        dos.writeInt(data.size());
        for(byte[]block:data){
            dos.writeInt(block.length);
            dos.write(block);
        }

        return bao.toByteArray();
    }

    public void serializeDOS(DataOutputStream dos) throws IOException {
        dos.write(type);
        dos.writeInt(data.size());
        for(byte[]block:data){
            dos.writeInt(block.length);
            dos.write(block);
        }
    }

    public void deserialize(byte[] input) throws IOException {
        data=new ArrayList<>();

        DataInputStream inputStream=new DataInputStream(new ByteArrayInputStream(input));
        type=inputStream.readByte();
        int count=inputStream.readInt();
        int size=0;
        byte[] buffer;
        for(int i=0;i<count;i++){
            size=inputStream.readInt();
            buffer=new byte[size];
            inputStream.read(buffer,0,size);
            data.add(buffer);
        }
    }

    public void deserializeDIS(DataInputStream input) throws IOException {
        data=new ArrayList<>();
        type=input.readByte();
        int count=input.readInt();
        int size=0;
        byte[] buffer;
        for(int i=0;i<count;i++){
            size=input.readInt();
            buffer=new byte[size];
            input.read(buffer,0,size);
            data.add(buffer);
        }
    }
}
