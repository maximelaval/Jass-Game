package ch.epfl.javass.jass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

import static ch.epfl.javass.jass.PlayerId.PLAYER_1;
import static ch.epfl.javass.jass.PlayerId.*;

public final class TestMain extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
        ps.put(PLAYER_1, new GraphicalPlayerAdapter());

        Player player2 =  new MctsPlayer(PLAYER_2, 123, 10_000);
        Player player3 = new MctsPlayer(PLAYER_3, 456, 10_000);
        Player player4 = new MctsPlayer(PLAYER_4, 789, 10_000);

        ps.put(PLAYER_2, new PacedPlayer(player2, 1));
        ps.put(PLAYER_3, new PacedPlayer(player3, 1));
        ps.put(PLAYER_4, new PacedPlayer(player4, 1));

//        ps.put(PLAYER_2, new MctsPlayer(PLAYER_2, 123, 10_000));
//        ps.put(PLAYER_3, new MctsPlayer(PLAYER_3, 456, 10_000));
//        ps.put(PLAYER_4, new MctsPlayer(PLAYER_4, 789, 10_000));



        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(i -> ns.put(i, i.name()));

        new Thread(() -> {
            JassGame g = new JassGame(0, ps, ns);
            while (! g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }).start();
    }
}