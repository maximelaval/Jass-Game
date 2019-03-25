package ch.epfl.javass.jass;

import java.util.Map;

public final class PacedPlayer implements Player {

    private Player underlyingPlayer;
    long minTime;

    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = (long)(minTime * 1000);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime = System.currentTimeMillis();
        Card card = underlyingPlayer.cardToPlay(state, hand);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (elapsedTime < minTime) {
            try {
                Thread.sleep(minTime - (elapsedTime));
            } catch (InterruptedException e) {/* ignore */}
        }
        return card;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
}
