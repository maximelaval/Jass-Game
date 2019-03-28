package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Let one works with cards packed in an int type.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class PackedCard {

    /**
     * Represents an invalid packed card.
     */
    public final static int INVALID = 0b111111;

    /**
     * Returns true if and only if the packed card has a valid value.
     *
     * @param pkCard the packed card to be checked.
     * @return whether the packed card is valid or not.
     */
    public static boolean isValid(int pkCard) {
        return ((pkCard & Bits32.mask(0, 4)) >= 0) && ((pkCard & Bits32.mask(0, 4)) <= 8)
                && (((pkCard & Bits32.mask(6, 26)) == 0) && (pkCard != INVALID));
    }

    /**
     * Returns the packed card of the given rank and color.
     *
     * @param c the given color.
     * @param r the given rank.
     * @return the packed card.
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), 4, c.ordinal(), 2);
    }

    /**
     * Returns the color of the given packed card.
     *
     * @param pkCard the packed card.
     * @return the color of the packed card.
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        int colorInt = Bits32.extract(pkCard, 4, 2);
        return Card.Color.values()[colorInt];
    }

    /**
     * Returns the rank of the given packed card.
     *
     * @param pkCard the packed card.
     * @return the rank of the packed card.
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        int rankInt = Bits32.extract(pkCard, 0, 4);
        return Card.Rank.values()[rankInt];
    }

    /**
     * Returns true if and only if the packed card pkCArdL is better than the packed card pkCardR,
     * knowing the trump color. if the twos cards are not comparable, returns false.
     *
     * @param trump   the trump color.
     * @param pkCardL the packed card to be compared with pkCardR.
     * @param pkCardR the packed card compared with pkCardL.
     * @return whether pkCardL is better than pkCardR.
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL) && isValid(pkCardR);
        if (color(pkCardL).equals(color(pkCardR))) {
            if (color(pkCardL).equals(trump)) {
                return rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal();
            } else {
                return rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
            }
        } else {
            return color(pkCardL).equals(trump);
        }
    }

    /**
     * Returns the value of the given packed card knowing the trump color.
     *
     * @param trump  the trump color.
     * @param pkCard the packed card.
     * @return the value of the packed card.
     */
    public static int points(Card.Color trump, int pkCard) {
        if (color(pkCard).equals(trump)) {
            switch (rank(pkCard)) {
                case NINE:
                    return 14;
                case TEN:
                    return 10;
                case JACK:
                    return 20;
                case QUEEN:
                    return 3;
                case KING:
                    return 4;
                case ACE:
                    return 11;
                default:
                    return 0;
            }
        } else {
            switch (rank(pkCard)) {
                case TEN:
                    return 10;
                case JACK:
                    return 2;
                case QUEEN:
                    return 3;
                case KING:
                    return 4;
                case ACE:
                    return 11;
                default:
                    return 0;
            }
        }
    }

    /**
     * Gives a written representation of the given packed card in the form :
     * "symbol of the color" plus "short rank name".
     *
     * @param pkCard the packed card.
     * @return the written representation.
     */
    public static String toString(int pkCard) {
        return color(pkCard).symbol + rank(pkCard);
    }
}
