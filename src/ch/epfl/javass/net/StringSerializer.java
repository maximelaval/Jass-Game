package ch.epfl.javass.net;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class to (de)serialize Jass commands.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class StringSerializer {

    private StringSerializer() {}


    /**
     * Returns the serialized given integer into a string.
     *
     * @param i the given integer.
     * @return the serialized integer.
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, 16);
    }

    /**
     * Returns the given string into the previously serialized integer.
     *
     * @param s the given serialized string.
     * @return the deserialized integer.
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    /**
     * Returns the serialized given integer into a string.
     *
     * @param l the given integer.
     * @return the serialized integer.
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, 16);
    }

    /**
     * Returns the given string into the previously serialized integer.
     *
     * @param s the given serialized string.
     * @return the deserialized integer.
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }

    /**
     * Returns the serialized given string.
     *
     * @param s the given string.
     * @return the serialized string.
     */
    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }

    /**
     * Returns the deserialized given string.
     *
     * @param s the given string.
     * @return the deserialized string.
     */
    public static String deserializeString(String s) {

        return new String(Base64.getDecoder().decode(s.getBytes(UTF_8)));
    }

    /**
     * Returns the combined string mad of the given strings separated by the given delimiter.
     *
     * @param delimiter the given delimiter.
     * @param strings the given strings.
     * @return the combined string.
     */
    public static String combine(String delimiter, String... strings) {
        return String.join(delimiter, strings);
    }

    /**
     * Returns an array of strings which are the result of the splitting of the given string according to the given delimiter.
     *
     * @param delimiter the given delimiter.
     * @param string the given string.
     * @return an array of strings made of the given split string.
     */
    public static String[] split(String delimiter, String string) {
        return string.split(delimiter);
    }

}
