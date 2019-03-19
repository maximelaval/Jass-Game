package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Represents the state of a turn.
 * @author lucas meier (283726)
 */
public final class TurnState {
    private TurnState(Card.Color trump, long pkScore, PlayerId firstPlayer) {
        int pkTrick = (trump.ordinal() << 30) | (firstPlayer.ordinal() << 28);
        this.pkCurrentTrick = pkTrick;
        this.pkCurrentScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards();
    }

    private long pkCurrentScore;
    private long pkUnplayedCards;
    private int pkCurrentTrick;

    private long pkUnplayedCards() {
        CardSet unPlayed = CardSet.EMPTY;
        for (int i = 0; i < PackedTrick.size(pkCurrentTrick); ++i) {
            unPlayed.add(Card.ofPacked(PackedTrick.card(pkCurrentTrick, i)));
        }
        return PackedCardSet.complement(unPlayed.packed());
    }

    public static TurnState initial(Card.Color trump, Score score, PlayerId firstPlayer) {
        return new TurnState(trump, score.packed(), firstPlayer);
    }

    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        if (PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick)) {
            return new TurnState(PackedTrick.trump(pkTrick), pkScore, PackedTrick.player(pkTrick, 0));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public long packedScore() {
        return pkCurrentScore;
    }

    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    public int packedTrick() {
        return pkCurrentTrick;
    }

    public Score score() {
        return Score.ofPacked(packedScore());
    }

    public CardSet unplayedCards() {
        return CardSet.ofPacked(packedUnplayedCards());
    }

    public Trick trick() {
        return Trick.ofPacked(packedTrick());
    }

    public boolean isTerminal() {
        if (trick().equals(Trick.INVALID)) {
            return true;
        } else {
            return false;
        }
    }

    public PlayerId nextPlayer() {
        if (trick().isFull()) {
            throw new IllegalStateException();
        } else {
            return PlayerId.values()[((Bits32.extract(pkCurrentTrick, 28, 2) + PackedTrick.size(pkCurrentTrick)) % 4 + 1) % 4];
        }
    }

//    public TurnState withNewCardPlayed(Card card) {
//        if (PackedTrick.isFull(pkCurrentTrick)) {
//            throw new IllegalStateException();
//        } else {
//
//
//        }
//    }
//
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            return ofPackedComponents(pkCurrentScore, pkUnplayedCards , PackedTrick.nextEmpty(pkCurrentTrick));
        }
    }
//
//    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
//        if (PackedTrick.isFull(pkCurrentTrick)) {
//            throw new IllegalStateException();
//        } else {
//
//        }
//    }


}

