package ch.epfl.javass.net;

import ch.epfl.javass.jass.*;

import java.util.Map;

public final class RemotePlayerClient implements Player, AutoCloseable {

    private final String hostName;

    public RemotePlayerClient(String hostName) {
        this.hostName = hostName;
    }



    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        return null;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

    }

    @Override
    public void updateHand(CardSet newHand) {

    }

    @Override
    public void setTrump(Card.Color trump) {

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

    @Override
    public void close() throws Exception {

    }
}
