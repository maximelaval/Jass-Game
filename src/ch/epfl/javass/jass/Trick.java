package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Represents a trick of a Jass game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Trick {

    /**
     * Represents an invalid trick.
     */
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);
    private final int pkTrick;

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * Returns the empty trick with the given trump and the given first player.
     *
     * @param trump       the given trump.
     * @param firstPlayer the given first player.
     * @return the empty trick with the given trump and the given first player.
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return ofPacked(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Returns the trick corresponding of the given packed trick.
     *
     * @param packed the given packed trick.
     * @return the trick corresponding of the given packed trick.
     */
    public static Trick ofPacked(int packed) {
        checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Returns the packed version of the given trick.
     *
     * @return the packed version of the given trick.
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * Returns the next empty trick following the given trick with the same trump and
     * the first player being the winning player of the given trick.
     *
     * @return the next empty trick following the given trick with the same trump and
     * the first player being the winning player of the given trick.
     */
    public Trick nextEmpty() {
        if (this.isFull()) {
            return new Trick(PackedTrick.nextEmpty(pkTrick));
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns true if the given trick is empty.
     *
     * @return whether the given trick is empty.
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * Returns true if the given trick is full.
     *
     * @return whether the given trick is full.
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * Returns true if the given trick is the last one of the turn.
     *
     * @return whether the given trick is the last one of the turn.
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * Returns the size of the given trick.
     *
     * @return the size of the given trick.
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * Returns the trump of the given trick.
     *
     * @return the trump of the given trick.
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * Returns the index of the given trick.
     *
     * @return the index of the given trick.
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * Returns the identity of the player at the given index in the trick.
     *
     * @param index the given index.
     * @return the identity of the player at the given index in the trick.
     */
    public PlayerId player(int index) {
        return PackedTrick.player(pkTrick, Preconditions.checkIndex(index, PlayerId.COUNT));
    }

    /**
     * Returns the card in the given trick at the given index.
     *
     * @param index the given index.
     * @return the card in the given trick at the given index.
     */
    public Card card(int index) {
            return Card.ofPacked(PackedTrick.card(pkTrick, Preconditions.checkIndex(index, this.size())));
    }

    /**
     * Returns the given trick containing an additional given card.
     *
     * @param c the given card.
     * @return the given trick containing an additional given card.
     */
    public Trick withAddedCard(Card c) {
        if (!this.isFull()) {
            return ofPacked(PackedTrick.withAddedCard(pkTrick, c.packed()));
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the base colour of the given trick.
     *
     * @return the base colour of the given trick.
     */
    public Color baseColor() {
        if (!this.isEmpty()) {
            return PackedTrick.baseColor(pkTrick);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the subset of cards of the given hand that can be played for the next card
     * in the given trick.
     *
     * @param hand the given hand.
     * @return the subset of cards of the given hand that can be played for the next card
     * in the given trick.
     */
    public CardSet playableCards(CardSet hand) {
        if (!this.isFull()) {
            return CardSet.ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the value of the given trick.
     *
     * @return the value of the given trick.
     */
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * Returns the identity of the leading player in the given trick.
     *
     * @return the identity of the leading player in the given trick.
     */
    public PlayerId winningPlayer() {
        if (!this.isEmpty()) {
            return PackedTrick.winningPlayer(pkTrick);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Trick) && (((Trick) obj).packed() == pkTrick);
    }

    @Override
    public int hashCode() {
        return this.packed();
    }

    @Override
    public String toString() {
        return PackedTrick.toString(pkTrick);
    }
}
