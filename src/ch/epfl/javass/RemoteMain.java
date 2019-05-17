package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is meant to be used by the remote player that will accept a connection from a local game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public class RemoteMain extends Application {

    /**
     * The main method of the remote player.
     *
     * @param args the program arguments (are not used).
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread serverGame = new Thread(() -> {
            System.out.println("La partie commencera à la connexion du client...");
            RemotePlayerServer playerServer = new RemotePlayerServer(new GraphicalPlayerAdapter());
            playerServer.run();
        });
        serverGame.setDaemon(true);
        serverGame.start();
    }
}
