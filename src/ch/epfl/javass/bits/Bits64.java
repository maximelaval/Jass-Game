package ch.epfl.javass.bits;

import static ch.epfl.javass.Preconditions.checkArgument;


/**
 * Let one works with 32 bits vectors stored in int variable.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Bits64 {

    private Bits64() {
    }

    /**
     * Creates a mask starting at "start" included and ending at "start + size"
     * excluded.
     *
     * @param start start of the mask.
     * @param size size of the mask.
     * @return returns the mask.
     */
    public static long mask(long start, long size) {
        checkArgument(((start >= 0) && (start <= Long.SIZE) && (size >= 0)
                && (start + size >= 0) && (start + size <= Long.SIZE)));
        return (size == Long.SIZE) ? -1L : ((1L << size) - 1L) << start;

    }

    /**
     * Extracts "start + size" bits starting at the position "start".
     *
     * @param bits original bit string.
     * @param start start position of the extraction.
     * @param size size of the extraction
     * @return the extracted number.
     */
    public static long extract(long bits, int start, int size) {
        checkArgument((start >= 0) && (size >= 0) && (start <= Long.SIZE)
                && (start + size >= 0) && (start + size <= Long.SIZE));
        return start == 0 ? bits & mask(0, size)
                : ((1L << size) - 1L) & (bits >> start);

    }

    /**
     * packs "v1" and "v2" into an int.
     *
     * @param v1 value 1.
     * @param s1 size of v1.
     * @param v2 value 2.
     * @param s2 size of v2.
     * @return the packed int.
     */
    public static long pack(long v1, long s1, long v2, long s2) {
        checkArgument((checkPackArg(v1, s1)) && (checkPackArg(v2, s2))
                && ((s1 + s2) <= Long.SIZE));
        long v2Alt = v2 << s1;
        return v2Alt | v1;
    }

    /**
     * Checks if value and size are valid arguments for pack methods.
     *
     * @param value the value to be checked.
     * @param size the size to be checked.
     * @return whether the arguments are valid.
     */
    private static boolean checkPackArg(long value, long size) {
        return (Long.toBinaryString(value).length() <= size)
                && (Long.toBinaryString(value).length() < Long.SIZE)
                && (Long.toBinaryString(value).length() > 0);

    }
}