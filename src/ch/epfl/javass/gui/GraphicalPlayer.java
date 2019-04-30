package ch.epfl.javass.gui;

import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import com.sun.media.jfxmedia.events.PlayerEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.naming.Binding;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GraphicalPlayer {

    private BorderPane mainPane;

    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ScoreBean scoreBean, TrickBean trickBean) {
         mainPane = new BorderPane(createTrickPane(trickBean), createScorePane(scoreBean, playerNames, ownId),
                null, null, null);


    }

    public Stage createStage() {
        Scene scene = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    private GridPane createScorePane(ScoreBean scoreBean, Map<PlayerId, String> playerNames, PlayerId ownId) {
        GridPane scorePane = new GridPane();

       // ReadOnlyIntegerProperty lastTrickProperty = ;

        String team1NameString = playerNames.get(PlayerId.PLAYER_1) + " et " +
                playerNames.get(PlayerId.PLAYER_3);

        String team2NameString = playerNames.get(PlayerId.PLAYER_2) + " et " +
                playerNames.get(PlayerId.PLAYER_4);


        Text turnPointsOwnId = new Text(Bindings.convert(scoreBean.turnPointsProperty(ownId.team())).get());
        Text turnPointsOtherId = new Text(Bindings.convert(scoreBean.turnPointsProperty(ownId.team().other())).get());
        Text team1Name = new Text(team1NameString);
        Text team2Name = new Text(team2NameString);
        Text gamePointOwnId = new Text(Bindings.convert(scoreBean.gamePointsProperty(ownId.team())).get());
        Text gamePointsOtherId = new Text(Bindings.convert(scoreBean.gamePointsProperty(ownId.team().other())).get());



        scorePane.add(team1Name, 0, 0);
        scorePane.add(team2Name, 0, 1);

        scorePane.add(turnPointsOwnId, 1, 0);
        scorePane.add(turnPointsOtherId, 1, 1);

        scorePane.add(new Text("/Total :"), 3, 0);
        scorePane.add(new Text("/Total :"), 3, 1);

        scorePane.add(gamePointOwnId, 4, 0);
        scorePane.add(gamePointsOtherId, 4, 1);


        return scorePane;
    }

    private GridPane createTrickPane(TrickBean trickBean) {
        return null;
    }

    private BorderPane createVictoryPanes(ScoreBean scoreBean) {
        return null;
    }
}
