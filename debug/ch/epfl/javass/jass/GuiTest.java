package ch.epfl.javass.jass;

import ch.epfl.javass.gui.GraphicalPlayer;
import ch.epfl.javass.gui.HandBean;
import ch.epfl.javass.gui.ScoreBean;
import ch.epfl.javass.gui.TrickBean;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

@SuppressWarnings("JavaDoc")
public final class GuiTest extends Application {
    @SuppressWarnings("JavaDoc")
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(p -> ns.put(p, p.name()));
//        ns.put(PlayerId.PLAYER_1, "Marie");
//        ns.put(PlayerId.PLAYER_2, "David");
//        ns.put(PlayerId.PLAYER_3, "Jean");
//        ns.put(PlayerId.PLAYER_4, "Alice");

        ScoreBean sB = new ScoreBean();
        TrickBean tB = new TrickBean();
        HandBean hb = new HandBean();
        ArrayBlockingQueue queue = new ArrayBlockingQueue(9);
        hb.setHand(CardSet.ALL_CARDS);
        GraphicalPlayer g =
                new GraphicalPlayer(PlayerId.PLAYER_2, ns, sB, tB, hb, queue);
        g.createStage().show();

        new AnimationTimer() {
            long now0 = 0;
            TurnState s = TurnState.initial(Card.Color.SPADE,
                    Score.INITIAL,
                    PlayerId.PLAYER_3);
            CardSet d = CardSet.ALL_CARDS;

            @Override
            public void handle(long now) {
                if (now - now0 < 1_000_000_000L || s.isTerminal())
                    return;
                now0 = now;

                s = s.withNewCardPlayed(d.get(0));
                d = d.remove(d.get(0));
                tB.setTrump(s.trick().trump());
                tB.setTrick(s.trick());

                if (s.trick().isFull()) {
                    s = s.withTrickCollected();
                    if (s.trick().index() == 1) {
                        sB.setTotalPoints(TeamId.TEAM_1, 1002);
                        System.out.println(sB.totalPointsProperty(TeamId.TEAM_2));
                    }
                    for (TeamId t : TeamId.ALL)
                        sB.setTurnPoints(t, s.score().turnPoints(t));
                }
            }
        }.start();
    }
}