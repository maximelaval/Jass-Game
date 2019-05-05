package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalPlayer {

    private final BorderPane mainPane;
    private ObservableMap<Card, Image> cardImageMap = FXCollections.observableHashMap();
    private ObservableMap<Card.Color, Image> colorImageMap = FXCollections.observableHashMap();
    private Map<Position, PlayerId> playerPosition = new HashMap<>();

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
            Text pointsLastTrick = new Text();
            pointsLastTrick.textProperty().bind(Bindings.format("(+ %d )", differenceTurnPoints));
            pointsLastTrick.visibleProperty().bind(differenceTurnPoints.greaterThan(0));
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

    private GridPane createTrickPane(PlayerId ownId, Map<PlayerId, String> playerNames, TrickBean trickBean) {
        GridPane trickPane = new GridPane();
        creatRequiredMaps(ownId);
        List<VBox> vBoxes = createVboxes(trickBean, playerNames);

        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty().bind(Bindings.valueAt(colorImageMap, trickBean.trumpProperty()));
        trumpImage.setFitWidth(101);
        trumpImage.setFitHeight(101);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

        trickPane.add(vBoxes.get(3), 0, 1, 1, 1);
        trickPane.add(vBoxes.get(2), 1, 0);
        trickPane.add(trumpImage, 1, 1);
        trickPane.add(vBoxes.get(0), 1, 2);
        trickPane.add(vBoxes.get(1), 2, 1, 1, 1);

        trickPane.setStyle("-fx-background-color: whitesmoke;" +
                "-fx-padding: 5px;" +
                "-fx-border-width: 3px 0px;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: gray;" +
                "-fx-alignment: center");
        return trickPane;
    }

    private void creatRequiredMaps(PlayerId ownId) {

        for (int i = 0; i < PlayerId.COUNT; i++) {
            playerPosition.put(Position.ALL.get(i), PlayerId.ALL.get((ownId.ordinal() + i) % PlayerId.COUNT));
        }

        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 9; r++) {
                cardImageMap.put(Card.of(Card.Color.ALL.get(c), Card.Rank.ALL.get(r)),
                        new Image("/card_" + c + "_" + r + "_160.png"));
            }
            colorImageMap.put(Card.Color.ALL.get(c), new Image("/trump_" + c + ".png"));
        }
    }

    private List<VBox> createVboxes(TrickBean trickBean, Map<PlayerId, String> playerNames) {
        List<ImageView> imageViewListList = new ArrayList<>();
        List<VBox> vBoxes = new ArrayList<>();
        List<StackPane> stackPaneList = new ArrayList<>();

        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty().bind(Bindings.valueAt(colorImageMap, trickBean.trumpProperty()));
        trumpImage.setFitWidth(101);
        trumpImage.setFitHeight(101);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

        for (int i = 0; i < 4; i++) {
            imageViewListList.add(new ImageView());
            imageViewListList.get(i).imageProperty().bind(Bindings.valueAt(cardImageMap,
                    Bindings.valueAt(trickBean.trickProperty(), playerPosition.get(Position.ALL.get(i)))));
            imageViewListList.get(i).setFitHeight(180);
            imageViewListList.get(i).setFitWidth(120);
            Rectangle rectangle = new Rectangle(120, 180);
            rectangle.setStyle("-fx-arc-width: 20;-fx-arc-height: 20;-fx-fill: transparent;-fx-stroke: lightpink;-fx-stroke-width: 5;-fx-opacity: 0.5;");
            rectangle.setEffect(new GaussianBlur(4));
//            rectangle.visibleProperty().bind(trickBean.winningPlayerProperty().getValue().equals(playerPosition.get(Position.ALL.get(i))));
            stackPaneList.add(new StackPane(imageViewListList.get(i), rectangle));
            vBoxes.add(new VBox(new Text(playerNames.get(playerPosition.get(Position.ALL.get(i)))), stackPaneList.get(i)));
            vBoxes.get(i).setStyle("-fx-padding: 5px; -fx-alignment: center;");
        }
        return vBoxes;
    }

    private StackPane createVictoryPanes(Map<PlayerId, String> playerNames, ScoreBean scoreBean) {
        Text textTeam1 = new Text();
        textTeam1.textProperty().bind(Bindings.format(" %s et %s ont gagné avec %d points contre %d",
                playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_3), scoreBean.totalPointsProperty(TeamId.TEAM_1),
                scoreBean.totalPointsProperty(TeamId.TEAM_2)));
        Text textTeam2 = new Text();
        textTeam2.textProperty().bind(Bindings.format(" %s et %s ont gagné avec %d points contre %d",
                playerNames.get(PlayerId.PLAYER_2), playerNames.get(PlayerId.PLAYER_4), scoreBean.totalPointsProperty(TeamId.TEAM_2),
                scoreBean.totalPointsProperty(TeamId.TEAM_1)));
        BorderPane pane1 = new BorderPane(textTeam1);
        BorderPane pane2 = new BorderPane(textTeam2);

        pane1.visibleProperty().bind(scoreBean.winningTeamProperty().isEqualTo(TeamId.TEAM_1));
        pane2.visibleProperty().bind(scoreBean.winningTeamProperty().isEqualTo(TeamId.TEAM_2));
        pane1.setStyle("-fx-font: 16 Optima; -fx-background-color: white");
        pane2.setStyle("-fx-font: 16 Optima; -fx-background-color: white");

        return new StackPane(pane1, pane2);
    }

    private String getTeamName(TeamId teamId, Map<PlayerId, String> playerNames) {
        return teamId.equals(TeamId.TEAM_1) ?
                playerNames.get(PlayerId.PLAYER_1) + " et " +
                        playerNames.get(PlayerId.PLAYER_3) + " : " :
                playerNames.get(PlayerId.PLAYER_2) + " et " +
                        playerNames.get(PlayerId.PLAYER_4) + " : ";
    }
}
