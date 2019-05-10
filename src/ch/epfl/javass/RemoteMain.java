package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.PrintingPlayer;
import ch.epfl.javass.jass.RandomPlayer;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class RemoteMain extends Application {

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
