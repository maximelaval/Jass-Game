package ch.epfl.javass.net;

import java.nio.ByteBuffer;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class StringSerializer {

    private StringSerializer() {}


    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i);
    }

    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s);
    }

    public static String serializeLong(long l) {
        return Long.toUnsignedString(l);
    }

    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s);
    }


    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }

    public static String deserializeString(String s) {
        byte[] bytes = s.getBytes(UTF_8);
        return new String(bytes, UTF_8);
    }

    public static String combine(String delimiter, String... strings) {
        return String.join(delimiter, strings);
    }

    public static String[] split(String string, String delimiter) {
        return string.split(delimiter);
    }
















}
