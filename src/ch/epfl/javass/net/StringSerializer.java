package ch.epfl.javass.net;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class StringSerializer {

    private StringSerializer() {}


    public static String serializeInt(int i) {
        //return Integer.toUnsignedString((i));
        return Integer.toHexString(i);
    }

    public static int deserializeInt(String s) {
        return Integer.parseInt(s, 16);
    }

    public static String serializeLong(long l) {
        return Long.toHexString(l);
    }

    public static long deserializeLong(String s) {
        return Long.parseLong(s, 16);
    }


    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }

    public static String deserializeString(String s) {

        byte[] decodedBytes = Base64.getDecoder().decode(s);
        return new String(decodedBytes, UTF_8);
    }

    public static String combine(String delimiter, String... strings) {
        return String.join(delimiter, strings);
    }

    public static String[] split(String delimiter, String string) {
        return string.split(delimiter);
    }
















}
