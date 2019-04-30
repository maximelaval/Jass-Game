package ch.epfl.javass.gui;

import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

public class GraphicalPlayer {

    public GraphicalPlayer(TeamId ownId, HashMap playerNames, ScoreBean scoreBean, TrickBean trickBean) {
        BorderPane mainPane = new BorderPane(createScorePane(scoreBean, playerNames, ownId), null, null, null, null);
        mainPane.topProperty().set(createTrickPane(trickBean));

    }

    public Stage createStage() {
        return null;
    }

    private GridPane createScorePane(ScoreBean scoreBean, HashMap playerNames, TeamId ownId) {
        GridPane scorePane = new GridPane();
        scoreBean.turnPointsProperty(ownId);

        SimpleIntegerProperty lastTrickProperty = ;

        String team1NameString = playerNames.get(PlayerId.PLAYER_1.ordinal()) + " et " +
                playerNames.get(PlayerId.PLAYER_3.ordinal());
        Text team1Name = new Text(team1NameString);
        String team2NameString = playerNames.get(PlayerId.PLAYER_2.ordinal()) + " et " +
                playerNames.get(PlayerId.PLAYER_4.ordinal());
        Text team2Name = new Text(team2NameString);

        scorePane.add(team1Name, 0, 0);
        scorePane.add(team2Name, 0, 1);


        return scorePane;
    }

    private GridPane createTrickPane(TrickBean trickBean) {
        return null;
    }

    private BorderPane createVictoryPanes(ScoreBean scoreBean) {
        return null;
    }
}
