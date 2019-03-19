package ch.epfl.javass.jass;

import java.util.Map;

public interface Player {

    abstract Card cardToPlay(TurnState state, CardSet hand);

    public default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){

    }

    public default void updateHand(CardSet newHand) {

    }

    public default void setTrump(Card.Color trump) {

    }

    public default void updateTrick(Trick newTrick) {

    }

    public default void updateScore(Score score) {

    }

    public default void setWinningTeam(TeamId winningTeam) {

    }
}
