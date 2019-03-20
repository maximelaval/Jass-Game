package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.*;

/**
 * Represents a trick of a Jass game.
 * @author Lucas Meier (283726)
 */
public final class Trick {

    private int pkTrick;

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    public final static Trick INVALID = new Trick(PackedTrick.INVALID);


    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return ofPacked(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    public static Trick ofPacked(int packed) {
        if (PackedTrick.isValid(packed)) {
            return new Trick(packed);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int packed() {
        return pkTrick;
    }

    public Trick nextEmpty() {
        if (this.isFull()) {
            return new Trick(PackedTrick.nextEmpty(pkTrick));
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    public int size() {
        return PackedTrick.size(pkTrick);
    }

    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    public int index() {
        return PackedTrick.index(pkTrick);
    }

    public PlayerId player(int index) {
        if ((index >= 0) && (index < 4)) {
            return PackedTrick.player(pkTrick, index);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Card card(int index) {
        if ((index >= 0) && (index < this.size())) {
            return Card.ofPacked(PackedTrick.card(pkTrick, index));
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Trick withAddedCard(Card c) {
        if (!this.isFull()) {
            return ofPacked(PackedTrick.withAddedCard(pkTrick, c.packed()));
        } else {
            throw new IllegalStateException();
        }
    }

    public Color baseColor() {
        if (!this.isEmpty()) {
            return PackedTrick.baseColor(pkTrick);
        } else {
            throw new IllegalStateException();
        }
    }

    public CardSet playableCards(CardSet hand) {
        if (!this.isFull()) {
            return CardSet.ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
        } else {
            throw new IllegalStateException();
        }
    }

    public int points() {
        return PackedTrick.points(pkTrick);
    }

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
