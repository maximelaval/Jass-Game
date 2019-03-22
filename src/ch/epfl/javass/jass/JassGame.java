package ch.epfl.javass.jass;

import java.util.*;

import static java.time.chrono.JapaneseEra.values;

public final class JassGame {

    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private  Map<PlayerId, Player>players;
   private  Map<PlayerId, String> playerNames;
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        this.players = java.util.Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = java.util.Collections.unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
     
    }

    public boolean isGameOver() {
       return (turnState.packedScore()>=Jass.WINNING_POINTS);
         }

    
    
    
    public void advanceToEndOfNextTrick() {
        if (!isGameOver()) {

        }
    }
}
