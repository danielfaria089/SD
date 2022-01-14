package common;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Frame {
    public static final byte BASIC=(byte)0;
    public static final byte LOGIN=(byte)1;
    public static final byte FLIGHT=(byte)2;
    public static final byte BOOKING=(byte)3;
    public static final byte ALL_FLIGHTS=(byte)4;
    public static final byte ACCOUNT_FLIGHTS=(byte)5;
    public static final byte CITIES=(byte)6;
    public static final byte STOPOVERS=(byte)7;
    public static final byte NOTIF=(byte)8;
    public static final byte CANCEL=(byte)9;
    public static final byte SPEC_BOOK=(byte)10;
    public static final byte CANCEL_DAY=(byte)11;
    // ...
    public static final byte END=(byte) 12;

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

    public Frame(byte[]frameBytes) throws IOException{
        this.deserialize(frameBytes);
    }

    public Frame(DataInputStream inputStream) throws IOException{
        this.deserialize(inputStream);
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

    public byte[] getDataAt(int index){
        return data.get(index);
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

    public void deserialize(byte[] input) throws IOException {
        DataInputStream inputStream=new DataInputStream(new ByteArrayInputStream(input));
        deserialize(inputStream);
    }

    public void deserialize(DataInputStream input) throws IOException {
        data=new ArrayList<>();
        type=input.readByte();
        int count=input.readInt();
        int size;
        byte[] buffer;
        for(int i=0;i<count;i++){
            size=input.readInt();
            buffer=new byte[size];
            input.read(buffer,0,size);
            data.add(buffer);
        }
    }
}