package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Represents the state of a turn of a Jass game.
 * @author Lucas Meier (283726)
 */
public final class TurnState {
    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkCurrentTrick = pkTrick;
        this.pkCurrentScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
    }

    private long pkCurrentScore;
    private long pkUnplayedCards;
    private int pkCurrentTrick;


    public static TurnState initial(Card.Color trump, Score score, PlayerId firstPlayer) {
        long allCards = PackedCardSet.ALL_CARDS;
        return new TurnState(score.packed(), allCards, Trick.firstEmpty(trump, firstPlayer).packed());
    }

    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        if (PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick)) {
            return new TurnState(pkScore, pkUnplayedCards, pkTrick);
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
        return (pkCurrentTrick == (PackedTrick.INVALID));

    }

    public PlayerId nextPlayer() {
        if (trick().isFull()) {
            throw new IllegalStateException();
        } else {
            return PlayerId.values()[(Bits32.extract(pkCurrentTrick, 28, 2) + PackedTrick.size(pkCurrentTrick)) % 4];
        }
    }

        public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            long updatedPkUnplayed = PackedCardSet.remove(pkUnplayedCards, card.packed());
            int pktrick = PackedTrick.withAddedCard(pkCurrentTrick, card.packed());
            return new TurnState(pkCurrentScore, updatedPkUnplayed, pktrick);
        }
    }

    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            long updatedScore = PackedScore.withAdditionalTrick(pkCurrentScore,
                    PackedTrick.winningPlayer(pkCurrentTrick).team(), PackedTrick.points(pkCurrentTrick));
            return new TurnState(updatedScore, pkUnplayedCards , PackedTrick.nextEmpty(pkCurrentTrick));
        }
    }

    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if (PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            TurnState result = withNewCardPlayed(card);
            if (result.trick().isFull()) {
                result = result.withTrickCollected();
            }
            return result;
        }
    }

}

