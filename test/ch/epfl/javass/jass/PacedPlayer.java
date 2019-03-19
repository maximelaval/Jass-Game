package ch.epfl.javass.jass;

import java.util.Map;

public final class PacedPlayer implements Player {

    public PacedPlayer(Player underlyingPlayer, double minTime) {

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        this.updateHand(newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        this.
    }

    @Override
    public void updateTrick(Trick newTrick) {

    }

    @Override
    public void updateScore(Score score) {

    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {

    }
}
