package ch.epfl.javass.gui;

import ch.epfl.javass.jass.*;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Represents a player that can be used in a graphical interface.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public class GraphicalPlayerAdapter implements Player {

    private final ArrayBlockingQueue<Card> queue;
    private final HandBean handBean;
    private final ScoreBean scoreBean;
    private final TrickBean trickBean;


    /**
     * Constructs a graphical player adapter by creating its required fields.
     */
    public GraphicalPlayerAdapter() {
        handBean = new HandBean();
        scoreBean = new ScoreBean();
        trickBean = new TrickBean();
        queue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card card;

        runLater(() -> handBean.setPlayableCards(state.trick().playableCards(hand)));
        try {
            card = queue.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }

        runLater(() ->   handBean.setPlayableCards(CardSet.EMPTY));
        return card;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        GraphicalPlayer graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean, handBean, queue);
        runLater(() -> graphicalPlayer.createStage().show());
    }

    @Override
    public void updateHand(CardSet newHand) {
        runLater(() -> handBean.setHand(newHand)
        );
    }

    @Override
    public void setTrump(Card.Color trump) {
        runLater(() -> trickBean.setTrump(trump));
    }

    @Override
    public void updateTrick(Trick newTrick) {
        runLater(() -> trickBean.setTrick(newTrick));
    }

    @Override
    public void updateScore(Score score) {
        runLater(() -> {
            for (TeamId teamId : TeamId.ALL) {
                scoreBean.setTurnPoints(teamId, score.turnPoints(teamId));
                scoreBean.setGamePoints(teamId, score.gamePoints(teamId));
                scoreBean.setTotalPoints(teamId, score.totalPoints(teamId));
            }
        });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        runLater(() -> scoreBean.setWinnigTeam(winningTeam));
    }
}
