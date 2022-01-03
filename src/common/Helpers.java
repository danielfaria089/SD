package common;

import java.nio.charset.StandardCharsets;

public class Helpers {

    public static final String IP="localhost";
    public static final int PORT=5678;

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value};
    }

    public static int fromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static byte[] charToBytes(char[] array){
        return new String(array).getBytes(StandardCharsets.UTF_8);
    }

    public static char[] bytesToChar(byte[] array){
        return new String(array,StandardCharsets.UTF_8).toCharArray();
    }

}
