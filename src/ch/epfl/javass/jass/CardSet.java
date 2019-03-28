package ch.epfl.javass.jass;

import java.util.List;
import java.util.StringJoiner;

import static ch.epfl.javass.jass.PackedCardSet.isValid;


/**
 * Represents a set of cards of a Jass game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class CardSet {

    /**
     * Represents an empty card set.
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    /**
     * Represents every cards in a card set.
     */
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);
     private final long pkCardSet;

    private CardSet(long pkCardSet) {
        this.pkCardSet = pkCardSet;
    }

    /**
     * Returns the card set composed of all the cards contained in the given list of cards.
     *
     * @param cards the given card list.
     * @return the card set made of the given cards.
     */
    public static CardSet of(List<Card> cards) {
        long pkCardSet = 0;
        for (Card card : cards) {
            pkCardSet = PackedCardSet.add(pkCardSet, card.packed());
        }
        return new CardSet(pkCardSet);
    }

    /**
     * Returns the card set corresponding to the given packed card set.
     *
     * @param packed the given packed card set.
     * @return the card set corresponding to the given packed card set.
     */
    public static CardSet ofPacked(long packed) {
        if (isValid(packed)) {
            return new CardSet(packed);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the packed version of the given card set.
     *
     * @return the packed version of the given card set.
     */
    public long packed() {
        return this.pkCardSet;
    }

    /**
     * Returns true if the given set of cards is empty.
     *
     * @return whether the given card set is empty.
     */
    public boolean isEmpty() {
        return this.pkCardSet == PackedCardSet.EMPTY;
    }

    /**
     * Returns the number of cards contained in the given card set.
     *
     * @return the number of cards contained in the given card set.
     */
    public int size() {
        return PackedCardSet.size(this.pkCardSet);
    }

    /**
     * Returns the card of the given card set at the given index.
     *
     * @param index the given index.
     * @return the card of the given card set at the given index.
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(pkCardSet, index));
    }

    /**
     * Adds the given card to the given card set.
     *
     * @param card the given card.
     * @return the updated card set.
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(pkCardSet, card.packed()));
    }

    /**
     * Removes the given card from the given card set.
     *
     * @param card the given card.
     * @return the updated card set.
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(pkCardSet, card.packed()));
    }

    /**
     * Returns true if and only if the given card set contains the given card.
     *
     * @param card the given card.
     * @return whether the given card set contains the given card.
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(pkCardSet, card.packed());
    }

    /**
     * Returns the complement of the given card set.
     *
     * @return the complement of the given card set.
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(pkCardSet));
    }

    /**
     * Returns the union of the two given card sets.
     *
     * @param that the second card set.
     * @return the union of the two card sets.
     */
    public CardSet union(CardSet that) {
        return new CardSet(PackedCardSet.union(pkCardSet, that.pkCardSet));
    }

    /**
     * Returns the intersection of the two given card sets.
     *
     * @param that the second card set.
     * @return the intersection of the two card sets.
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(PackedCardSet.intersection(pkCardSet, that.pkCardSet));
    }

    /**
     * Returns the difference between the first and the second given card set.
     *
     * @param that the second card set.
     * @return the difference between the two card sets.
     */
    public CardSet difference(CardSet that) {
        return new CardSet(PackedCardSet.difference(pkCardSet, that.pkCardSet));
    }

    /**
     * Returns the subset of cards of the given card set composed only of the given color.
     *
     * @param color the given color.
     * @return the computed subset of cards.
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(pkCardSet, color));
    }

    @Override
    public boolean equals(Object that0) {
        return (that0 instanceof CardSet) && (((CardSet) that0).packed() == pkCardSet);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(pkCardSet);
    }

    @Override
    public String toString() {
        assert (isValid(pkCardSet));
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < PackedCardSet.size(pkCardSet); ++i) {
            j.add(Card.ofPacked(PackedCardSet.get(pkCardSet, i)).toString());
        }
        return j.toString();
    }
}