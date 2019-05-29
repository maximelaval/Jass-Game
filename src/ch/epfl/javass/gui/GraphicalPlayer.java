package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Represents a window that contains the GUI of the local player.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public class GraphicalPlayer {
    private final static int SET_TRUMP_IMAGE_DIMENSION = 101;
    private final static int SET_CARD_TRICK_HEIGHT = 180;
    private final static int SET_CARD_TRICK_WIDTH = 120;
    private final static int SET_CARD_HAND_HEIGHT = 120;
    private final static int SET_CARD_HAND_WIDTH = 80;
    private final StackPane mainStack;
    private  final ObservableMap<Card, Image> cardImageMap = FXCollections.observableHashMap();
    private final  ObservableMap<Card.Color, Image> colorImageMap = FXCollections.observableHashMap();
    private final Map<Position, PlayerId> playerPosition = new HashMap<>();


    /**
     * Constructs a graphical player with the given identity, the list of the player names, the bean score,
     * the bean trick, the bean hand and the queue that allows the class to communicate with the GraphicalPlayerAdapter.
     *
     * @param ownId       the given identity.
     * @param playerNames the given list of player names.
     * @param scoreBean   the bean score.
     * @param trickBean   the bean trick.
     * @param handBean    the bean hand.
     * @param queue       the given queue.
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ScoreBean scoreBean, TrickBean trickBean,
                           HandBean handBean, ArrayBlockingQueue<Card> queue) {
        Pane mainPane = new BorderPane(createTrickPane(ownId, playerNames, trickBean), createScorePane(scoreBean, playerNames),
                null, createHandPane(handBean, queue), null);

        mainStack = new StackPane(mainPane);
        createVictoryPanes(playerNames, scoreBean); // In the method createdVictoryPanes we create 2 victory panes for TEAM 1 and TEAM 2 and add them to the mainStack.

    }

    /**
     * Returns the stage of the window.
     *
     * @return the stage of the window.
     */
    public Stage createStage() {
        Scene scene = new Scene(mainStack);
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

            gamePoint.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(teamId)));

            scorePane.add(team1Name, 0, teamId.ordinal());
            scorePane.add(turnPoints, 1, teamId.ordinal());
            differenceTurnPoints.addListener((o,oV,nV)-> {
                if(nV.intValue() > 0 && !scorePane.getChildren().contains(pointsLastTrick)) {

                    scorePane.add(pointsLastTrick, 2, teamId.ordinal());
                } if(nV.intValue() <= 0) {
                    scorePane.getChildren().remove(pointsLastTrick);
                }

            });
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
        List<VBox> vBoxes = createTrickVboxes(trickBean, playerNames);

        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty().bind(Bindings.valueAt(colorImageMap, trickBean.trumpProperty()));
        trumpImage.setFitWidth(SET_TRUMP_IMAGE_DIMENSION);
        trumpImage.setFitHeight(SET_TRUMP_IMAGE_DIMENSION);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

        trickPane.add(vBoxes.get(3), 0, 0, 1, 3);
        trickPane.add(vBoxes.get(2), 1, 0);
        trickPane.add(trumpImage, 1, 1);
        trickPane.add(vBoxes.get(0), 1, 2);
        trickPane.add(vBoxes.get(1), 2, 0, 1, 3);

        trickPane.setStyle("-fx-background-color: whitesmoke;" +
                "-fx-padding: 5px;" +
                "-fx-border-width: 3px 0px;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: gray;" +
                "-fx-alignment: center");
        return trickPane;
    }

    /*
     * This method creates a mapping between each player and
     * their position on the game table (BOTTOM,RIGHT,TOP,LEFT),
     * and a second mapping between each card and their corresponding image.

     */
    private void creatRequiredMaps(PlayerId ownId) {
        for (int i = 0; i < PlayerId.COUNT; i++) {
            playerPosition.put(Position.ALL.get(i), PlayerId.ALL.get((ownId.ordinal() + i) % PlayerId.COUNT));

        }

        for (int c = 0; c < Card.Color.COUNT; c++) {
            for (int r = 0; r < Card.Rank.COUNT; r++) {
                cardImageMap.put(Card.of(Card.Color.ALL.get(c), Card.Rank.ALL.get(r)),
                        new Image("/card_" + c + "_" + r + "_160.png"));
            }
            colorImageMap.put(Card.Color.ALL.get(c), new Image("/trump_" + c + ".png"));
        }
    }
    /*
     * This method creates 4 VBox , for i=0 it represents
     * the vbox for the player owndId , so the player at the BOTTOM.

     */
    private List<VBox> createTrickVboxes(TrickBean trickBean, Map<PlayerId, String> playerNames) {
        List<VBox> vBoxes = new ArrayList<>();

        for (int i = 0; i < PlayerId.COUNT; i++) {
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.valueAt(cardImageMap,
                    Bindings.valueAt(trickBean.trickProperty(), playerPosition.get(Position.ALL.get(i)))));
            imageView.setFitHeight(SET_CARD_TRICK_HEIGHT);
            imageView.setFitWidth(SET_CARD_TRICK_WIDTH);
            Rectangle rectangle = new Rectangle(SET_CARD_TRICK_WIDTH, SET_CARD_TRICK_HEIGHT);
            rectangle.setStyle("-fx-arc-width: 20;-fx-arc-height: 20;-fx-fill: transparent;-fx-stroke: lightpink;-fx-stroke-width: 5;-fx-opacity: 0.5;");
            rectangle.setEffect(new GaussianBlur(4));
            rectangle.visibleProperty().bind(trickBean.winningPlayerProperty().isEqualTo(playerPosition.get(Position.ALL.get(i))));
            StackPane stackPane =new StackPane(imageView,rectangle);

            Text text = new Text(playerNames.get(playerPosition.get(Position.ALL.get(i))));
            text.setStyle("-fx-font: 14 Optima;");
            if (i == 0) {
                vBoxes.add(new VBox(stackPane, text));
            } else {
                vBoxes.add(new VBox(text, stackPane));
            }
            vBoxes.get(i).setStyle("-fx-padding: 5px; -fx-alignment: center;");
        }
        return vBoxes;
    }

    private void createVictoryPanes(Map<PlayerId, String> playerNames, ScoreBean scoreBean) {

        BorderPane victoryPane;
        for (TeamId teamId : TeamId.ALL) {
            Text textTeam = new Text();
            textTeam.textProperty().bind(Bindings.format( getTeamName(teamId,playerNames) +" ont gagné avec %d points contre %d"
                    , scoreBean.totalPointsProperty(teamId),
                    scoreBean.totalPointsProperty(teamId.other())));

            victoryPane = new BorderPane(textTeam);

            victoryPane.visibleProperty().bind(scoreBean.winningTeamProperty().isEqualTo(teamId));

            victoryPane.setStyle("-fx-font: 16 Optima; -fx-background-color: white");

            mainStack.getChildren().add(victoryPane);
        }
    }

    private HBox createHandPane(HandBean handBean, ArrayBlockingQueue<Card> queue) {
        HBox pane = new HBox();

        for (int i = 0; i < Jass.HAND_SIZE; i++) {
            int j = i;
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.valueAt(cardImageMap,
                    Bindings.valueAt(handBean.handProperty(), i)));
            imageView.setFitHeight(SET_CARD_HAND_HEIGHT);
            imageView.setFitWidth(SET_CARD_HAND_WIDTH);
            pane.getChildren().add(imageView);

            BooleanProperty isPlayable = new SimpleBooleanProperty();
            isPlayable.bind(Bindings.createBooleanBinding(() ->
                            handBean.playableCardsProperty().contains(handBean.handProperty().get(j)),
                    handBean.playableCardsProperty(), handBean.handProperty()));
            imageView.opacityProperty().bind(Bindings.when(isPlayable).then(1).otherwise(0.2));

            imageView.setOnMouseClicked(e -> {
                try {
                    queue.put(handBean.handProperty().get(j));
                } catch (InterruptedException x) {
                    throw new Error(x);
                }
            });

            imageView.disableProperty().bind(Bindings.when(isPlayable).then(false).otherwise(true));
        }

        pane.setStyle("-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");
        return pane;
    }

    private String getTeamName(TeamId teamId, Map<PlayerId, String> playerNames) {
        return teamId.equals(TeamId.TEAM_1) ?
                playerNames.get(PlayerId.PLAYER_1) + " et " +
                        playerNames.get(PlayerId.PLAYER_3) + " : " :
                playerNames.get(PlayerId.PLAYER_2) + " et " +
                        playerNames.get(PlayerId.PLAYER_4) + " : ";
    }
}