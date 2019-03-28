package ch.epfl.javass;

/**
 * Define a few checking methods.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Preconditions {
    private Preconditions() {

    }

    /**
     * Returns true if the given argument is valid.
     *
     * @param b true if the given argument is valid.
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException("");
        }
    }

    /**
     * Returns true if the index and size given are both valid.
     *
     * @param index the given index.
     * @param size  the given size.
     * @return true if the index and size given are both valid.
     */
    public static int checkIndex(int index, int size) {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException("for index " + index);
        } else {
            return index;
        }
    }
}
