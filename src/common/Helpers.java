package common;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Random;

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

    public static int intFromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static byte[] charToBytes(char[] array){
        return new String(array).getBytes(StandardCharsets.UTF_8);
    }

    public static char[] bytesToChar(byte[] array){
        return new String(array,StandardCharsets.UTF_8).toCharArray();
    }

    public static byte[] localDateToBytes(LocalDate date){
        return date.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static LocalDate localDateFromBytes(byte[] data){
        return LocalDate.parse(new String(data,StandardCharsets.UTF_8));
    }

    public static String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 7;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static boolean verifyDate(LocalDate date,String dateText){
        String[] data_r = date.toString().split("-");
        String[] data = dateText.split("/");
        return data[0].equals(data_r[2]) && data[1].equals(data_r[1]) && data[2].equals(data_r[0]);
    }

}
