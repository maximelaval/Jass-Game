package ch.epfl.javass.jass;

import java.util.Map;

public final class PacedPlayer implements Player {

    private Player underLyingPlayer;
    double minTime;

    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underLyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime = System.currentTimeMillis();
        Card card = underLyingPlayer.cardToPlay(state, hand);
        long endTime = System.currentTimeMillis();
        if (endTime - startTime < minTime) {
            try {
                Thread.sleep((long)minTime - (endTime - startTime));
            } catch (InterruptedException e) {/* ignore */}
        }
        return card;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underLyingPlayer.setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        underLyingPlayer.updateHand(newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        underLyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underLyingPlayer.updateTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        underLyingPlayer.updateScore(score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underLyingPlayer.setWinningTeam(winningTeam);
    }
}
