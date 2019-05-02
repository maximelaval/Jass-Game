package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GraphicalPlayer {

    private BorderPane mainPane;

    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ScoreBean scoreBean, TrickBean trickBean) {
        mainPane = new BorderPane(createTrickPane(ownId, playerNames, trickBean), createScorePane(scoreBean, playerNames, ownId),
                null, null, null);
        StackPane victoryPanes = createVictoryPanes(playerNames, scoreBean);

    }

    public Stage createStage() {
        Scene scene = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    private GridPane createScorePane(ScoreBean scoreBean, Map<PlayerId, String> playerNames, PlayerId ownId) {
        GridPane scorePane = new GridPane();

//         ReadOnlyIntegerProperty lastTrickProperty = ;
//         scoreBean.turnPointsProperty(ownId.team()).addListener();

        String team1NameString = playerNames.get(PlayerId.PLAYER_1) + " et " +
                playerNames.get(PlayerId.PLAYER_3) + " : ";

        String team2NameString = playerNames.get(PlayerId.PLAYER_2) + " et " +
                playerNames.get(PlayerId.PLAYER_4) + " : ";


        Text turnPointsOwnId = new Text(Bindings.convert(scoreBean.turnPointsProperty(ownId.team())).get());
        Text turnPointsOtherId = new Text(Bindings.convert(scoreBean.turnPointsProperty(ownId.team().other())).get());
        Text team1Name = new Text(getTeamName(TeamId.TEAM_1, playerNames));
        Text team2Name = new Text(getTeamName(TeamId.TEAM_2, playerNames));
        Text gamePointOwnId = new Text(Bindings.convert(scoreBean.gamePointsProperty(ownId.team())).get());
        Text gamePointsOtherId = new Text(Bindings.convert(scoreBean.gamePointsProperty(ownId.team().other())).get());


        scorePane.add(team1Name, 0, 0);
        scorePane.add(team2Name, 0, 1);

        scorePane.add(turnPointsOwnId, 1, 0);
        scorePane.add(turnPointsOtherId, 1, 1);

        scorePane.add(new Text(" / Total : "), 3, 0);
        scorePane.add(new Text(" / Total : "), 3, 1);

        scorePane.add(gamePointOwnId, 4, 0);
        scorePane.add(gamePointsOtherId, 4, 1);

        scorePane.setStyle("-fx-font: 16 Optima;" +
                "-fx-background-color: lightgray;" +
                "-fx-padding: 5px;" +
                "-fx-alignment: center");


        return scorePane;
    }

    private GridPane createTrickPane(PlayerId ownId, Map<PlayerId, String > playerNames, TrickBean trickBean) {
        GridPane trickPane = new GridPane();


        ImageView leftImage =  trickBean.trickProperty() == null ? new ImageView() : new ImageView("/card_0_0_160.png");
        ImageView topImage = new ImageView("/card_0_1_160.png");
        ImageView rightImage = new ImageView("/card_0_2_160.png");
        ImageView bottomImage = new ImageView("/card_0_3_160.png");
        ImageView trumpImage = new ImageView("/trump_0.png");

//        System.out.println(trickBean.trumpProperty().toString());
//        ImageView trumpImage = new ImageView(trickBean.trumpProperty().getName());

        leftImage.setFitHeight(180);
        topImage.setFitHeight(180);
        rightImage.setFitHeight(180);
        bottomImage.setFitHeight(180);
        leftImage.setFitWidth(120);
        topImage.setFitWidth(120);
        rightImage.setFitWidth(120);
        bottomImage.setFitWidth(120);
        trumpImage.setFitWidth(101);
        trumpImage.setFitHeight(101);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

//        final ObservableMap<ImageView, Card> imageCardToCard = new ObservableMap<ImageView, Card>() {
//        };

        final Map<ImageView, PlayerId> playerPosition = new HashMap<>();
        playerPosition.put(bottomImage, ownId);
        playerPosition.put(rightImage, PlayerId.ALL.get((ownId.ordinal() + 1) % 4));
        playerPosition.put(topImage, PlayerId.ALL.get((ownId.ordinal() + 2) % 4));
        playerPosition.put(leftImage, PlayerId.ALL.get((ownId.ordinal() + 3) % 4));

        VBox left = new VBox(leftImage , new Text(playerNames.get(playerPosition.get(leftImage))));
        VBox top = new VBox(topImage, new Text(playerNames.get(playerPosition.get(topImage))));
        VBox right = new VBox(rightImage, new Text(playerNames.get(playerPosition.get(rightImage))));
        VBox bottom = new VBox(bottomImage, new Text(playerNames.get(ownId)));


        trickPane.add(left, 0, 1, 1, 1);
        trickPane.add(top, 1, 0);
        trickPane.add(trumpImage, 1, 1);
        trickPane.add(bottom, 1, 2);
        trickPane.add(right, 2, 1, 1, 1);

        trickPane.setStyle("-fx-background-color: whitesmoke;" +
                "-fx-padding: 5px;" +
                "-fx-border-width: 3px 0px;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: gray;" +
                "-fx-alignment: center");


        return trickPane;

    }

    private StackPane createVictoryPanes(Map<PlayerId, String> playerNames, ScoreBean scoreBean) {

//        Text text = new Text(getTeamName(scoreBean.winningTeamProperty().get(), playerNames) + " ont gagné avec " +
//                scoreBean.totalPointsProperty(scoreBean.winningTeamProperty().get()) + " points contre " +
//                scoreBean.totalPointsProperty(scoreBean.winningTeamProperty().get().other())); // faux car winningteam correspond a l'equipe qui mene le pli actuel
//        BorderPane pane1 = new BorderPane();
//        BorderPane pane2 = new BorderPane();
//        StackPane victoryPane = new StackPane(pane1, pane2);
//

        return null;
    }

    private String getTeamName(TeamId teamId, Map<PlayerId, String> playerNames) {
        return teamId.equals(TeamId.TEAM_1) ?
                playerNames.get(PlayerId.PLAYER_1) + " et " +
                playerNames.get(PlayerId.PLAYER_3) + " : " :
                playerNames.get(PlayerId.PLAYER_2) + " et " +
                playerNames.get(PlayerId.PLAYER_4) + " : ";
    }
}
