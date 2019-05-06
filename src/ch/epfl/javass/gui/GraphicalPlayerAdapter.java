package ch.epfl.javass.gui;

import ch.epfl.javass.jass.*;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

public class GraphicalPlayerAdapter implements Player {

    private HandBean handBean;
    private ScoreBean scoreBean;
    private TrickBean trickBean;
    GraphicalPlayer graphicalPlayer;
    ArrayBlockingQueue<Card> queue = new ArrayBlockingQueue<>(1);


    public GraphicalPlayerAdapter() {
//        handBean = new HandBean();

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        return null;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean);
        runLater(() -> { graphicalPlayer.createStage().show(); });
    }

    @Override
    public void updateHand(CardSet newHand) {
        runLater(() -> handBean.setHand(newHand));
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
//        runLater(() -> {scoreBean.setTurnPoints(, score.turnPoints()));
//            scoreBean.setTotalPoints(, score.turnPoints()));
//             scoreBean.setTotalPoints(, score.turnPoints())); });

    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        runLater(() -> scoreBean.setWinnigTeam(winningTeam));
    }
}
