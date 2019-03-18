//package ch.epfl.javass.jass;
//
///**
// * Represents the state of a turn.
// * @author lucas meier (283726)
// */
//public final class TurnState {
//    private TurnState(Card.Color trump, long pkScore, PlayerId firstPlayer) {
//        int pkTrick = (trump.ordinal() << 30) | (firstPlayer.ordinal() << 28);
//        this.pkCurrentTrick = pkTrick;
//        this.pkCurrentScore = pkScore;
//    }
//
//    private long pkCurrentScore;
//    private long pkUnplayedCards;
//    private int pkCurrentTrick;
//
//    public TurnState initial(Card.Color trump, Score score, PlayerId firstPlayer) {
//        return new TurnState(trump, score.packed(), firstPlayer);
//    }
//
//    public TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
//        if (PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick)) {
//            return new TurnState(PackedTrick.trump(pkTrick), pkScore, PackedTrick.player(pkTrick, 0));
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }
//
//    public long packedScore() {
//        return pkCurrentScore;
//    }
//
//    public long packedUnplayedCards() {
//        return pkUnplayedCards;
//    }
//
//    public int packedTrick() {
//        return pkCurrentTrick;
//    }
//
//    public Score score() {
//        return Score.ofPacked(packedScore());
//    }
//
//    public CardSet unplayedCards() {
//        return CardSet.ofPacked(pkUnplayedCards);
//    }
//
//    public Trick trick() {
//        return Trick.ofPacked(pkCurrentTrick);
//    }
//
//    public boolean isTerminal() {
//        if (trick().equals(Trick.INVALID)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public PlayerId nextPlayer() {
//        if (trick().isFull()) {
//            throw new IllegalStateException();
//        } else {
//            return ;
//        }
//    }
//
//    public TurnState withNewCardPlayed(Card card) {
//
//    }
//
//    public TurnState withTrickCollected() {
//
//    }
//
//    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
//
//    }
//
//
//
//
//
//
//
//}
