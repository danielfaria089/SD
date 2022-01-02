package common;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frame {
    private byte type;
    private List<byte[]> data;

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

    public void deserizalize(byte[] input) throws IOException {
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

}
