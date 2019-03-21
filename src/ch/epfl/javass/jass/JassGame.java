package ch.epfl.javass.jass;

import java.util.*;

import static java.time.chrono.JapaneseEra.values;

public final class JassGame {

    private TurnState turnState;

    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        // … reste du constructeur
    }

    public boolean isGameOver() {

    }

    public void advanceToEndOfNextTrick() {
        if (!isGameOver()) {

        }
    }
}
