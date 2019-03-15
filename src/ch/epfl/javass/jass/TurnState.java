package ch.epfl.javass.jass;

/**
 * Represents the state of a turn.
 * @author lucas meier (283726)
 */
public final class TurnState {
    private TurnState(Card.Color trump, PackedScore pkScore, PlayerId firstPlayer) {
        this.pkCurrentTrick = Trick.ofPacked()
        this.pkCurrentScore = pkScore;
    }

    private PackedScore pkCurrentScore;
    private PackedCardSet pkUnplayedCards;
    private PackedTrick pkCurrentTrick;

    public TurnState initial(Card.Color trump, Score score, PlayerId firstPlayer) {
        return new TurnState()
    }

    public TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        if (PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick)) {
            return new TurnState(PackedTrick.trump(pkTrick), pkScore, PackedTrick.player(pkTrick, 0));
        } else {
            throw new IllegalArgumentException();
        }
    }






}
