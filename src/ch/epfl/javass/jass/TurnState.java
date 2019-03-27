package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Represents the state of a turn of a Jass game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class TurnState {
    private long pkCurrentScore;
    private long pkUnplayedCards;
    private int pkCurrentTrick;

    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkCurrentTrick = pkTrick;
        this.pkCurrentScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
    }

    /**
     * Returns the initial state corresponding to a game turn where the trump, the score and the initial player are
     * the ones given.
     *
     * @param trump       the given trump.
     * @param score       the given score.
     * @param firstPlayer the given initial player.
     * @return the initial state corresponding to a game turn where the trump, the score and the initial player are
     * the ones given.
     */
    public static TurnState initial(Card.Color trump, Score score, PlayerId firstPlayer) {
        long allCards = PackedCardSet.ALL_CARDS;
        return new TurnState(score.packed(), allCards, Trick.firstEmpty(trump, firstPlayer).packed());
    }

    /**
     * Returns the state given its different components.
     *
     * @param pkScore         the given score.
     * @param pkUnplayedCards the given packed card set of the unplayed cards
     * @param pkTrick         the given packed trick.
     * @return the state given its different components.
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        if (PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick)) {
            return new TurnState(pkScore, pkUnplayedCards, pkTrick);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the packed version of the current score.
     *
     * @return the packed version of the current score.
     */
    public long packedScore() {
        return pkCurrentScore;
    }

    /**
     * Returns the packed version of the card set of the unplayed cards.
     *
     * @return the packed version of the card set of the unplayed cards.
     */
    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    /**
     * Returns the packed verion of the current trick.
     *
     * @return the packed verion of the current trick.
     */
    public int packedTrick() {
        return pkCurrentTrick;
    }

    /**
     * Retursn the current score.
     *
     * @return the current score.
     */
    public Score score() {
        return Score.ofPacked(packedScore());
    }

    /**
     * Returns the card set of the unplayed cards.
     *
     * @return the card set of the unplayed cards.
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(packedUnplayedCards());
    }

    /**
     * Retursn the current trick.
     *
     * @return the current trick.
     */
    public Trick trick() {
        return Trick.ofPacked(packedTrick());
    }

    /**
     * Returns true if and only if the last trick has been played.
     *
     * @return whether the last trick has been played.
     */
    public boolean isTerminal() {
        return (pkCurrentTrick == (PackedTrick.INVALID));
    }

    /**
     * Returns the identity of the next player that has to play or throws an exception if the current trick is full.
     *
     * @return the identity of the next player that has to play.
     */
    public PlayerId nextPlayer() {
        if (trick().isFull()) {
            throw new IllegalStateException();
        } else {
            return PlayerId.values()[(Bits32.extract(pkCurrentTrick, 28, 2) + PackedTrick.size(pkCurrentTrick)) % 4];
        }
    }

    /**
     * Returns the state corresponding to the given one after the next player has played the given card.
     *
     * @param card the given card.
     * @return the state corresponding to the given one after the next player has played the given card.
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            long updatedPkUnplayed = PackedCardSet.remove(pkUnplayedCards, card.packed());
            int pktrick = PackedTrick.withAddedCard(pkCurrentTrick, card.packed());
            return new TurnState(pkCurrentScore, updatedPkUnplayed, pktrick);
        }
    }

    /**
     * Returns the state corresponding to the given state after the current trick has been collected.
     *
     * @return the state corresponding to the given state after the current trick has been collected.
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(pkCurrentTrick)) {
            throw new IllegalStateException();
        } else {
            long updatedScore = PackedScore.withAdditionalTrick(pkCurrentScore,
                    PackedTrick.winningPlayer(pkCurrentTrick).team(), PackedTrick.points(pkCurrentTrick));
            return new TurnState(updatedScore, pkUnplayedCards, PackedTrick.nextEmpty(pkCurrentTrick));
        }
    }

    /**
     * Returns the state of the given state after the player has played the given card and if this makes the trick full,
     * it returns the state after the trick has been collected.
     *
     * @param card the given card.
     * @return the state of the given state after the player has played the given card and if this makes the trick full,
     * it returns the state after the trick has been collected.
     */
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

