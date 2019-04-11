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
    public static final int INVALID = mask(0, Integer.SIZE);
    private static final int LAST_TRICK = 8;
    private static final int CARD_SIZE = 6;
    private static final int MAX_TRICK_SIZE = 4;
    private static final int INDEX_START = 24;
    private static final int PLAYER_AND_TRUMP_SIZE = 2;
    private static final int TRUMP_START = 30;
    private static final int PLAYER_START = 28;
    private PackedTrick() {

    }

    /**
     * Check the validity of a packed trick.
     *
     * @param pkTrick the packed trick to be checked.
     * @return whether the packed trick is valid.
     */
    public static boolean isValid(int pkTrick) {

        final int index = extract(pkTrick, INDEX_START, MAX_TRICK_SIZE);

        final int ZTH_CARD = extract(pkTrick, 0, 6);
        final int FST_CARD = extract(pkTrick, 6, 6);
        final int SND_CARD = extract(pkTrick, 12, 6);
        final int TRD_CARD = extract(pkTrick, 18, 6);

        return (
                ((index <= LAST_TRICK) && (index >= 0)) &&
                        (((TRD_CARD != PackedCard.INVALID) &&
                                (SND_CARD != PackedCard.INVALID) &&
                                (FST_CARD != PackedCard.INVALID) &&
                                (ZTH_CARD != PackedCard.INVALID)) ||

                                ((TRD_CARD == PackedCard.INVALID) &&
                                        (SND_CARD != PackedCard.INVALID) &&
                                        (FST_CARD != PackedCard.INVALID) &&
                                        (ZTH_CARD != PackedCard.INVALID)) ||

                                ((TRD_CARD == PackedCard.INVALID) &&
                                        (SND_CARD == PackedCard.INVALID) &&
                                        (FST_CARD != PackedCard.INVALID) &&
                                        (ZTH_CARD != PackedCard.INVALID)) ||

                                ((TRD_CARD == PackedCard.INVALID) &&
                                        (SND_CARD == PackedCard.INVALID) &&
                                        (FST_CARD == PackedCard.INVALID) &&
                                        (ZTH_CARD != PackedCard.INVALID)) ||

                                ((TRD_CARD == PackedCard.INVALID) &&
                                        (SND_CARD == PackedCard.INVALID) &&
                                        (FST_CARD == PackedCard.INVALID) &&
                                        (ZTH_CARD == PackedCard.INVALID))));
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
                PLAYER_AND_TRUMP_SIZE, trump.ordinal(), PLAYER_AND_TRUMP_SIZE);
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
        return isLast(pkTrick) ? INVALID : pack(
                PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE,
                index(pkTrick) + 1, MAX_TRICK_SIZE,
                winningPlayer(pkTrick).ordinal(), PLAYER_AND_TRUMP_SIZE,
                trump(pkTrick).ordinal(), PLAYER_AND_TRUMP_SIZE);
    }

    /**
     * Returns true if the given packed trick is the last one of the turn.
     *
     * @param pkTrick the given packed trick.
     * @return whether the given packed trick is the last one of the turn.
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == LAST_TRICK;
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
        return Color.values()[extract(pkTrick, TRUMP_START, PLAYER_AND_TRUMP_SIZE)];
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
        int firstPlayer = extract(pkTrick, PLAYER_START, PLAYER_AND_TRUMP_SIZE);
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
        return extract(pkTrick, INDEX_START, MAX_TRICK_SIZE);
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
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, TRUMP_START, PLAYER_AND_TRUMP_SIZE)),
                        card(pkTrick, 1), j)) {
            i = 1;
            j = card(pkTrick, 1);
        }

        if (card(pkTrick, 2) != PackedCard.INVALID &&
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, TRUMP_START, PLAYER_AND_TRUMP_SIZE)),
                        card(pkTrick, 2), j)) {
            i = 2;
            j = card(pkTrick, 2);
        }

        if (card(pkTrick, 3) != PackedCard.INVALID &&
                PackedCard.isBetter(Color.ALL.get(extract(pkTrick, TRUMP_START, PLAYER_AND_TRUMP_SIZE)),
                        card(pkTrick, 3), j)) {
            i = 3;
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
