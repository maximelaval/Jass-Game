package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

import java.util.StringJoiner;

import static ch.epfl.javass.bits.Bits32.*;
import static ch.epfl.javass.jass.Jass.LAST_TRICK_ADDITIONAL_POINTS;
import static ch.epfl.javass.jass.PackedCard.color;

/**
 * Let one works with tricks packed in a int type.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class PackedTrick {

    /**
     * Represents an invalid packed trick.
     */
    public static final int INVALID = mask(0, 32);
    private static final int CARD_SIZE = 6;
    private static final int MAX_TRICK_SIZE = 4;

    private PackedTrick() {

    }

    /**
     * Check the validity of a packed trick.
     *
     * @param pkTrick the packed trick to be checked.
     * @return whether the packed trick is valid.
     */
    public static boolean isValid(int pkTrick) {
        return (
                (
                        (extract(pkTrick, 24, MAX_TRICK_SIZE) <= 8) &&
                                (extract(pkTrick, 24, MAX_TRICK_SIZE) >= 0)

                ) && (

                        ((extract(pkTrick, 18, 6) != PackedCard.INVALID) &&
                                (extract(pkTrick, 12, 6) != PackedCard.INVALID)

                                && (extract(pkTrick, 6, 6) != PackedCard.INVALID) &&
                                (extract(pkTrick, 0, 6) != PackedCard.INVALID)

                        ) ||

                                ((extract(pkTrick, 18, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 12, 6) != PackedCard.INVALID)
                                        && (extract(pkTrick, 6, 6) != PackedCard.INVALID) &&
                                        (extract(pkTrick, 0, 6) != PackedCard.INVALID)

                                ) ||

                                ((extract(pkTrick, 18, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 12, 6) == PackedCard.INVALID)
                                        && (extract(pkTrick, 6, 6) != PackedCard.INVALID) &&
                                        (extract(pkTrick, 0, 6) != PackedCard.INVALID)

                                ) ||

                                ((extract(pkTrick, 18, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 12, 6) == PackedCard.INVALID)
                                        && (extract(pkTrick, 6, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 0, 6) != PackedCard.INVALID)

                                ) ||

                                ((extract(pkTrick, 18, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 12, 6) == PackedCard.INVALID)
                                        && (extract(pkTrick, 6, 6) == PackedCard.INVALID) &&
                                        (extract(pkTrick, 0, 6) == PackedCard.INVALID)
                                )
                )
        );
    }

    /**
     * Returns the empty packed trick with the given trump and the given first player.
     *
     * @param trump       the given trump.
     * @param firstPlayer the given first player.
     * @return the empty packed trick with the given trump and the given first player.
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, PackedCard.INVALID, CARD_SIZE, 0, MAX_TRICK_SIZE, firstPlayer.ordinal(),
                2, trump.ordinal(), 2);
    }

    /**
     * Returns the next empty packed trick following the given packed trick with the same trump and
     * the first player being the winning player of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the next empty packed trick following the given trick with the same trump and
     * the first player being the winning player of the given packed trick.
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        assert isFull(pkTrick);
        if (isLast(pkTrick)) {
            return INVALID;
        } else {
            return pack(
                    PackedCard.INVALID, CARD_SIZE,
                    PackedCard.INVALID, CARD_SIZE,
                    PackedCard.INVALID, CARD_SIZE,
                    PackedCard.INVALID, CARD_SIZE,
                    index(pkTrick) + 1, MAX_TRICK_SIZE,
                    winningPlayer(pkTrick).ordinal(), 2,
                    trump(pkTrick).ordinal(), 2);
        }
    }

    /**
     * Returns true if the given packed trick is the last one of the turn.
     *
     * @param pkTrick the given packed trick.
     * @return whether the given packed trick is the last one of the turn.
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == 8;
    }

    /**
     * Returns true if the given packed trick is empty.
     *
     * @param pkTrick the given packed trick
     * @return whether the given packed trick is empty.
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == 0;
    }

    /**
     * Returns true if the given packed trick is full.
     *
     * @param pkTrick the given packed trick.
     * @return whether the given packed trick is the full.
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == MAX_TRICK_SIZE;
    }

    /**
     * Returns the size of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the size of the given packed trick.
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        for (int i = MAX_TRICK_SIZE; i > 0; --i) {
            if (PackedCard.isValid(card(pkTrick, i - 1))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the trump of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the trump of the given packed trick.
     */
    public static Card.Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Color.values()[extract(pkTrick, 30, 2)];
    }

    /**
     * Returns the identity of the player at the given index in the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @param index   the given index.
     * @return the identity of the player at the given index in the given packed trick.
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);
        int firstPlayer = extract(pkTrick, 28, 2);
        int player = (firstPlayer + index) % PlayerId.COUNT;
        return PlayerId.values()[player];
    }

    /**
     * Returns the index of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the index of the given packed trick.
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return extract(pkTrick, 24, MAX_TRICK_SIZE);
    }

    /**
     * Returns the packed version of the card in the given packed trick at the given index.
     *
     * @param pkTrick the given packed trick.
     * @param index   the given index.
     * @return the packed version of the card in the given packed trick at the given index.
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick);
        return extract(pkTrick, CARD_SIZE * index, CARD_SIZE);
    }

    /**
     * Returns the given packed trick containing an additional given packed card.
     *
     * @param pkTrick the given packed trick.
     * @param pkCard  the given packed card.
     * @return the given packed trick containing an additional given packed card.
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert (!isFull(pkTrick));
        int var = CARD_SIZE * size(pkTrick);
        return (~mask(var, CARD_SIZE) & pkTrick) | (pkCard << var);
    }

    /**
     * Returns the base colour of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the base colour of the given packed trick.
     */
    public static Card.Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        assert (!isEmpty(pkTrick));
        return color(card(pkTrick, 0));
    }

    /**
     * Returns the packed subset of cards of the given packed hand that can be played for the next card
     * in the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @param pkHand  the given packed hand.
     * @return the packed subset of cards of the given packed hand that can be played for the next card
     * in the given packed trick.
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert isValid(pkTrick);
        assert !isFull(pkTrick);

        if (isEmpty(pkTrick)) return pkHand;

        Color trump = trump(pkTrick);
        long tr = PackedCardSet.subsetOfColor(pkHand, trump);

        long best;
        if (baseColor(pkTrick) == trump(pkTrick)) {
            best = tr;
        } else {
            best = tr;
            for (int i = 1; i < size(pkTrick); ++i) {
                int pkCard = card(pkTrick, i);
                if (PackedCard.color(pkCard) == trump(pkTrick))
                    best = PackedCardSet.intersection(best, PackedCardSet.trumpAbove(pkCard));
            }
        }

        long base = PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
        long j = PackedCardSet.singleton(PackedCard.pack(trump, Rank.JACK));
        long rest = PackedCardSet.difference(pkHand, PackedCardSet.union(base, tr));

        if (base == j) return pkHand;
        else if (!PackedCardSet.isEmpty(base)) return PackedCardSet.union(base, best);
        else if (!PackedCardSet.isEmpty(rest)) return PackedCardSet.union(rest, best);
        else if (!PackedCardSet.isEmpty(best)) return best;
        else return pkHand;
    }

    /**
     * Returns the value of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the value of the given packed trick.
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int result = 0;
        for (int i = 0; i < MAX_TRICK_SIZE; i++) {
            if (card(pkTrick, i) != PackedCard.INVALID) {
                result += PackedCard.points(trump(pkTrick), card(pkTrick, i));
            }
        }
        if (isLast(pkTrick)) {
            assert isValid(pkTrick);
            result += LAST_TRICK_ADDITIONAL_POINTS;
        }
        return result;
    }

    /**
     * Returns the identity of the leading player in the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return the identity of the leading player in the given packed trick.
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        int i = 0;
        int j = card(pkTrick, 0);

        if (card(pkTrick, 1) != PackedCard.INVALID &&
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, 30, 2)), card(pkTrick, 1), j)) {
            i = 1;
            j = card(pkTrick, 1);
        }

        if (card(pkTrick, 2) != PackedCard.INVALID &&
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, 30, 2)), card(pkTrick, 2), j)) {
            i = 2;
            j = card(pkTrick, 2);
        }

        if (card(pkTrick, 3) != PackedCard.INVALID &&
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, 30, 2)), card(pkTrick, 3), j)) {
            i = 3;
            j = card(pkTrick, 3);
        }

        return player(pkTrick, i);
    }


    /**
     * Returns a written representation of the given packed trick.
     *
     * @param pkTrick the given packed trick.
     * @return a written representation of the given packed trick.
     */
    public static String toString(int pkTrick) {
        assert (isValid(pkTrick));

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); ++i) {
            j.add(Card.ofPacked(card(pkTrick, i)).toString());
        }
        return j.toString();
    }
}
