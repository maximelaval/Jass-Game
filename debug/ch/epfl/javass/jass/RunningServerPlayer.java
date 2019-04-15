package ch.epfl.javass.jass;

import ch.epfl.javass.net.RemotePlayerServer;

public class RunningServerPlayer {

    public static void main(String[] args) {

        RemotePlayerServer playerServer = new RemotePlayerServer(new PrintingPlayer(new RandomPlayer(2012)));
        playerServer.run();
    }

}
