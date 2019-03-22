package ch.epfl.javass.jass;

import java.util.*;

public final class JassGame {

    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;


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
            if (turnState.trick().index() == 0) {
                shuffleAndDistribute();
                startTurn();
            } else {
                nextPlayerToPlay();
            }
        }
    }

    private void shuffleAndDistribute() {
        List<Card> deck = constructCardList();
        Collections.shuffle(deck, shuffleRng);
        for (int i = 0; i < 4; ++i){
            players.get(i).updateHand(CardSet.of(deck.subList(i * 9, i * 9 + 8)));
        }
    }

    private void startTurn() {

    }

    private void nextPlayerToPlay() {

    }

    private List<Card> constructCardList() {
        List<Card> list = Collections.emptyList();
        for (int i = 0; i < Card.Color.COUNT; i++) {
            for (int j = 0; j < Card.Rank.COUNT; ++i) {
                list.add(Card.of(Card.Color.values()[i], Card.Rank.values()[j]));
            }
        }
        return list;
    }
}
