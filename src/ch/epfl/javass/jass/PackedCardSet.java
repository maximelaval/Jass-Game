package ch.epfl.javass.jass;

import java.util.StringJoiner;

import static ch.epfl.javass.bits.Bits64.extract;
import static ch.epfl.javass.bits.Bits64.mask;

/**
 * Let one works with card sets packed in a long type.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class PackedCardSet {

    /**
     * Represents an empty packed card set.
     */
    public static final long EMPTY = 0L;

    /**
     * Represents a card set containing every cards.
     */
    public static final long ALL_CARDS = 0b111111111000000011111111100000001111111110000000111111111L;

    final private static long[][] trumpTable = computeTrump();
    final private static long[] subColorTable = computeSubColor();

    private PackedCardSet() {
    }


    /**
     * Returns true if and only if the packed card set represents a valid value.
     *
     * @param pkCardSet the packed card set.
     * @return whether the packed card set has a valid value.
     */
    public static boolean isValid(long pkCardSet) {
        return (extract(pkCardSet, 9, 7) == 0) &&
                (extract(pkCardSet, 25, 7) == 0) &&
                (extract(pkCardSet, 41, 7) == 0) &&
                (extract(pkCardSet, 57, 7) == 0);
    }

    /**
     * Returns the set of all cards better than the given packed card,
     * knowing that it is a trump card.
     *
     * @param pkCard the given packed card.
     * @return the packed set of all cards better than the given packed card.
     */
    public static long trumpAbove(int pkCard) {
        assert (isValid(pkCard));
        return trumpTable[PackedCard.color(pkCard).ordinal()][PackedCard.rank(pkCard).ordinal()];
    }

    /**
     * Returns a table made of the packed game cards that will be used by trumpAbove.
     *
     * @return a table made of the packed game cards that will be used by trumpAbove.
     */
    private static long[][] computeTrump() {
        long[][] ans = new long[Card.Color.COUNT][Card.Rank.COUNT];
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {

                int card = PackedCard.pack(c, r);

                long set = EMPTY;
                for (Card.Rank k : Card.Rank.ALL) {
                    int other = PackedCard.pack(c, k);
                    if (PackedCard.isBetter(c, other, card)) {
                        set = add(set, other);
                    }
                }
                ans[c.ordinal()][r.ordinal()] = set;
            }
        }
        return ans;
    }

    /**
     * Returns the packed set of cards containing only the one given packed card.
     *
     * @param pkCard the given packed card.
     * @return the packed set of cards containing only the one given card.
     */
    public static long singleton(int pkCard) {
        assert (isValid(pkCard));
        return mask(pkCard, 1);
    }

    /**
     * Returns true if the given packed set of cards is empty.
     *
     * @param pkCardSet the given packed card set.
     * @return whether the given packed card set is empty.
     */
    public static boolean isEmpty(long pkCardSet) {
        assert (isValid(pkCardSet));
        return pkCardSet == EMPTY;
    }

    /**
     * Returns the number of cards contained in the given packed card set.
     *
     * @param pkCardSet the given packed card set.
     * @return the number of cards contained in the given packed card set.
     */
    public static int size(long pkCardSet) {
        assert (isValid(pkCardSet));
        return Long.bitCount(pkCardSet);
    }

    /**
     * Returns the packed card version of the given packed card set at the given index.
     *
     * @param pkCardSet the given packed card set.
     * @param index     the given index.
     * @return the packed card version of the given packed card set at the given index.
     */
    public static int get(long pkCardSet, int index) {
        assert (isValid(pkCardSet) && pkCardSet != EMPTY &&
                index >= 0 && index < size(pkCardSet));
        for (int i = 0; i < index; i++) {
            pkCardSet = pkCardSet ^ Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * Adds the given packed card to the given packed card set.
     *
     * @param pkCardSet the given packed card set.
     * @param pkCard    the given packed card.
     * @return the updated packed card set.
     */
    public static long add(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet));
        return pkCardSet | mask(pkCard, 1);
    }

    /**
     * Removes the given packed card from the given packed card set.
     *
     * @param pkCardSet the given packed card set.
     * @param pkCard    the given packed card.
     * @return the updated packed card set.
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet));
        return pkCardSet & ~mask(pkCard, 1);
    }

    /**
     * Returns true if and only if the given packed card set contains the given packed card.
     *
     * @param pkCardSet the given packed card set.
     * @param pkCard    the given packed card.
     * @return whether the given packed card set contains the given packed card.
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet));
        return (extract(pkCardSet, pkCard, 1)) == 1;
    }

    /**
     * Returns the complement of the given packed card set.
     *
     * @param pkCardSet the given packed card set.
     * @return the complement of the given packed card set.
     */
    public static long complement(long pkCardSet) {
        assert (isValid(pkCardSet));
        return pkCardSet ^ ALL_CARDS;
    }

    /**
     * Returns the union of the two given packed card sets.
     *
     * @param pkCardSet1 the first packed card set.
     * @param pkCardSet2 the second packed card set.
     * @return the union of the two packed card sets.
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1));
        assert (isValid(pkCardSet2));
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Returns the intersection of the two given packed card sets.
     *
     * @param pkCardSet1 the first packed card set.
     * @param pkCardSet2 the second packed card set.
     * @return the intersection of the two packed card sets.
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1));
        assert (isValid(pkCardSet2));
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Returns the difference between the first and the second given packed card set.
     *
     * @param pkCardSet1 the first packed card set.
     * @param pkCardSet2 the second packed card set.
     * @return the difference between the two packed card sets.
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1));
        assert (isValid(pkCardSet2));
        return pkCardSet1 & complement(pkCardSet2);
    }

    /**
     * Returns the subset of cards of the given packed card set composed only of the given color.
     *
     * @param pkCardSet the given packed card set.
     * @param color     the given color.
     * @return the computed subset of cards.
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert (isValid(pkCardSet));
        return pkCardSet & subColorTable[color.ordinal()];
    }

    /**
     * Compute a table made of some cards that will be used by subsetOfColor().
     *
     * @return the computed table.
     */
    private static long[] computeSubColor() {
        long[] t = new long[4];
        for (int i = 0; i < t.length; ++i) {
            t[i] = mask(i * 16, 9);
        }
        return t;
    }

    /**
     * Returns a written representation of the given packed card set.
     *
     * @param pkCardSet the given packed card set.
     * @return the written representation.
     */
    public static String toString(long pkCardSet) {
        assert (isValid(pkCardSet));

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkCardSet); ++i) {
            j.add(Card.ofPacked(get(pkCardSet, i)).toString());
        }
        return j.toString();
    }
}