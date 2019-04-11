package ch.epfl.javass.bits;

import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Let one works with 32 bits vectors stored in int variable.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Bits32 {

    private Bits32() {
    }

    /**
     * creates a mask starting at "start" included and ending at "start + size"
     * excluded.
     *
     * @param start start of the mask.
     * @param size size of the mask.
     * @return returns the mask.
     */
    public static int mask(int start, int size) {
        checkArgument(((start >= 0) && (start <= Integer.SIZE) && (size >= 0)
                && (start + size >= 0) && (start + size <= Integer.SIZE)));
        return (size == Integer.SIZE) ? -1 : ((1 << size) - 1) << start;
    }

    /**
     * Extracts "start + size" bits starting at the position "start".
     *
     * @param bits original bit string.
     * @param start start position of the extraction.
     * @param size size of the extraction
     * @return the extracted number.
     */
    public static int extract(int bits, int start, int size) {
        checkArgument(((start >= 0) && (start <= Integer.SIZE) && (size >= 0)
                && (start + size >= 0) && (start + size <= Integer.SIZE)));
        return start == 0 ? bits & mask(0, size)
                : ((1 << size) - 1) & (bits >> start);
    }

    /**
     * packs the values "v1" and "v2" with size s1 and s2 respectively into an int.
     *
     * @param v1 value 1.
     * @param s1 size of v1.
     * @param v2 value 2.
     * @param s2 size of v2.
     * @return the packed int.
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        checkArgument((checkPackArg(v1, s1)) && (checkPackArg(v2, s2))
                && ((s1 + s2) <= Integer.SIZE));

        int v2Alt = v2 << s1;
        return v2Alt | v1;
    }

    /**
     * packs v1, v2, v3 with size s1, s2, s3 respectively into an int.
     *
     * @param v1 value 1.
     * @param s1 size of v1.
     * @param v2 value 2.
     * @param s2 size of v2.
     * @param v3 value 3
     * @param s3 size of v3.
     * @return int the packed int.
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        checkArgument((checkPackArg(v1, s1)) && (checkPackArg(v2, s2))
                && (checkPackArg(v3, s3)) && ((s1 + s2 + s3) <= Integer.SIZE));

        int v3Alt = v3 << s1 + s2;
        int v2Alt = v2 << s1;

        return v3Alt | v2Alt | v1;
    }

    /**
     * packs v1, v2, v3, v4, v5, v6, v7 into an int.
     *
     * @param v1 value 1.
     * @param s1 size of v1.
     * @param v2 value 2.
     * @param s2 size of v2.
     * @param v3 value 3
     * @param s3 size of v3.
     * @param v4 value 4.
     * @param s4 size of v4.
     * @param v5 value 5.
     * @param s5 size of v5.
     * @param v6 value 6
     * @param s6 size of v6.
     * @param v7 value 7.
     * @param s7 size of v7
     * @return int the packed int.
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
                           int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        checkArgument((checkPackArg(v1, s1)) && (checkPackArg(v2, s2))
                && (checkPackArg(v3, s3)) && (checkPackArg(v4, s4))
                && (checkPackArg(v5, s5)) && (checkPackArg(v6, s6))
                && (checkPackArg(v7, s7))
                && ((s1 + s2 + s3 + s4 + s5 + s6 + s7) <= Integer.SIZE));

        int v7Alt = v7 << (s1 + s2 + s3 + s4 + s5 + s6);
        int v6Alt = v6 << (s1 + s2 + s3 + s4 + s5);
        int v5Alt = v5 << (s1 + s2 + s3 + s4);
        int v4Alt = v4 << (s1 + s2 + s3);
        int v3Alt = v3 << (s1 + s2);
        int v2Alt = v2 << (s1);
        return v1 | v2Alt | v3Alt | v4Alt | v5Alt | v6Alt | v7Alt;

    }

    /**
     * Checks if value and size are valid arguments for pack methods.
     *
     * @param value the value to be checked.
     * @param size the size to be checked.
     * @return boolean whether the arguments are valid.
     */
    private static boolean checkPackArg(int value, int size) {
        return (Integer.toBinaryString(value).length() <= size)
                && (Integer.toBinaryString(value).length() < Integer.SIZE)
                && (Integer.toBinaryString(value).length() > 0);

    }
}