package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a 36 cards game card.
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Card {

    /**
     * Defines a card color.
     * @author Lucas Meier (283726)
     */
    public enum Color {
        SPADE("\u2660"),
        HEART("\u2661"),
        DIAMOND("\u2662"),
        CLUB("\u2663")
        ;

        protected final String symbol;
        private Color(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Represents all colours.
         */
        public static final List<Color> ALL =
                Collections.unmodifiableList(Arrays.asList(values()));

        /**
         * The number of different colours.
         */
        public static final int COUNT = 4;

        /**
         * Returns a written representation of a colour.
         * @return a written representation of a colour.
         */
        public String toString() {
            return this.symbol;
        }
    }

    /**
     * Defines a card rank.
     * @author Lucas Meier (283726)
     */
    public enum Rank {
        SIX("6", 0),
        SEVEN("7", 1),
        EIGHT("8", 2),
        NINE("9", 7),
        TEN("10", 3),
        JACK("J", 8),
        QUEEN("Q", 4),
        KING("K", 5),
        ACE("A", 6)
        ;

        private final String symbol;
        private int trumpOrdinal;
        private Rank(String symbol, int trumpOrdinal) {
            this.symbol = symbol;
            this.trumpOrdinal = trumpOrdinal;
        }

        /**
         * Represents all ranks.
         */
        public static final List<Rank> ALL =
                Collections.unmodifiableList(Arrays.asList(values()));

        /**
         * The number of different ranks.
         */
        public static final int COUNT = 9;

        /**
         * Returns the position of the trump card.
         * @return the position of the trump card.
         */
        public int trumpOrdinal() {
            return this.trumpOrdinal;
        }

        /**
         * Return a written representation of a rank.
         * @return a written representation of a rank.
         */
        public String toString() {
            return this.symbol;
        }
    }

    private final int pkCard;
    private Card(Color c, Rank r) {
        this.pkCard = PackedCard.pack(c, r);
    }
    private Card(int packed) {
        this.pkCard = packed;
    }

    /**
     * Returns the card of the given Color and given Rank.
     * @param c the given Color.
     * @param r the given Rank.
     * @return the card of the given Color and given Rank.
     */
    public static Card of(Color c, Rank r) {
        Card card = new Card(c, r);
        return card;
    }

    /**
     * Returns the Card of the given packed version of the same Card.
     * Throws IllegalArgument Exception if packed is not valid.
     *
     * @param packed the packed version of the card.
     * @return the Card of the given packed version of the same Card.
     */
    public static Card ofPacked(int packed) {
        if (PackedCard.isValid(packed)) {
            return new Card(packed);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returs the packed version of the Card.
     * @return the packed version of the Card.
     */
    public int packed() {
        return pkCard;
    }

    /**
     * Returns the color of the Card.
     * @return the color of the Card.
     */
    public Color color() {
        return PackedCard.color(pkCard);
    }

    /**
     * Returns the Rank of the Card.
     * @return the Rank of the Card.
     */
    public Rank rank() {
        return PackedCard.rank(pkCard);
    }

    /**
     * Returns true if and only if "this" is a better Card than "that" knowing that the trump color is "trump".
     * @param trump the trump color.
     * @param that the card to be compared with "this".
     * @return whether the card the method is applied to is better than the card in argument.
     */
    public boolean isBetter(Color trump, Card that ) {
        if (this.color().equals(that.color())) {
            if (this.color().equals(trump)) {
                if (this.rank().trumpOrdinal() > that.rank().trumpOrdinal()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (this.rank().ordinal() > that.rank().ordinal()) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (this.color().equals(trump)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Returns the points of the card knowing that the trump color is trump.
     * @param trump the trump color.
     * @return the points of the card.
     */
    public int points(Color trump) {
        return PackedCard.points(trump, pkCard);
    }

    @Override
    public boolean equals(Object that0) {
        if ((this.hashCode() == that0.hashCode()) && (that0 instanceof Card) && (((Card)that0).pkCard == this.pkCard)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return packed();
    }

    @Override
    public String toString() {
        return PackedCard.color(pkCard).symbol + PackedCard.rank(pkCard);
    }
}
