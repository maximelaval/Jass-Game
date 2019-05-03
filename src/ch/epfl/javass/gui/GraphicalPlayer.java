package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.StringJoiner;

public class GraphicalPlayer {

    private BorderPane mainPane;

    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ScoreBean scoreBean, TrickBean trickBean) {
        mainPane = new BorderPane(createTrickPane(ownId, playerNames, trickBean), createScorePane(scoreBean, playerNames),
                null, null, null);
        StackPane victoryPanes = createVictoryPanes(playerNames, scoreBean);

    }

    public Stage createStage() {
        Scene scene = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    private GridPane createScorePane(ScoreBean scoreBean, Map<PlayerId, String> playerNames) {
        GridPane scorePane = new GridPane();

        for (TeamId teamId : TeamId.ALL) {
            IntegerProperty differenceTurnPoints = new SimpleIntegerProperty();
            Text turnPoints = new Text();
            Text gamePoint = new Text();
            scoreBean.turnPointsProperty(teamId).addListener((o, oV, nV) -> differenceTurnPoints.set(nV.intValue() - oV.intValue()));

            Text team1Name = new Text(getTeamName(teamId, playerNames));
            turnPoints.textProperty().bind(Bindings.convert(scoreBean.turnPointsProperty(teamId)));
            StringProperty s = new SimpleStringProperty();
            differenceTurnPoints.addListener((o, oV, nV) -> s.set("(+" + o.getValue() + " )"));
            Text pointsLastTrick = new Text();
            pointsLastTrick.textProperty().bind(s);
            gamePoint.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(teamId)));

            scorePane.add(team1Name, 0, teamId.ordinal());
            scorePane.add(turnPoints, 1, teamId.ordinal());
            scorePane.add(pointsLastTrick, 2, teamId.ordinal());
            scorePane.add(new Text(" / Total : "), 3, teamId.ordinal());
            scorePane.add(gamePoint, 4, teamId.ordinal());
        }

        scorePane.setStyle("-fx-font: 16 Optima;" +
                "-fx-background-color: lightgray;" +
                "-fx-padding: 5px;" +
                "-fx-alignment: center");
        return scorePane;
    }

    private GridPane createTrickPane(PlayerId ownId, Map<PlayerId, String > playerNames, TrickBean trickBean) {
        GridPane trickPane = new GridPane();

        final Map<Position, PlayerId> playerPosition = new HashMap<>();
        playerPosition.put(Position.BOTTOM, ownId);
        playerPosition.put(Position.RIGHT, PlayerId.ALL.get((ownId.ordinal() + 1) % 4));
        playerPosition.put(Position.TOP, PlayerId.ALL.get((ownId.ordinal() + 2) % 4));
        playerPosition.put(Position.LEFT, PlayerId.ALL.get((ownId.ordinal() + 3) % 4));

        ObservableMap<Card, ImageView> cardImageMap = FXCollections.observableHashMap();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 9; r++) {
                cardImageMap.put(Card.of(Card.Color.ALL.get(c), Card.Rank.ALL.get(r)),
                        new ImageView("/card_" + c + "_" + r + "_160.png"));
            }
        }


       // Bindings.valueAt(cardImageMap, leftImage.imageProperty());


        ImageView leftImage = new ImageView("/card_0_0_160.png");
//        leftImage.imageProperty().bind(Bindings.valueAt(cardImageMap, Bindings.valueAt(trickBean.trickProperty(), PlayerId.PLAYER_1)));
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

        final ObservableMap<ImageView, Card> imageCardToCard = FXCollections.observableHashMap();
//        imageCardToCard.put()



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
