package ch.epfl.javass.jass;

import java.util.*;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public final class JassGame {

    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private CardSet[] playerHands;


    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {

        this.players = java.util.Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = java.util.Collections.unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.turnState = TurnState.initial(Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT)), Score.INITIAL, firstPlayer());
        playerHands = new CardSet[4];

    }

    public boolean isGameOver() {
        return ((turnState.score().gamePoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS) ||
                (turnState.score().gamePoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS)) ;
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
            players.get(i).updateHand(CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8)));
            playerHands[i] = CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8));
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

    private PlayerId firstPlayer() {
        if (turnState.trick().index() == 0 && turnState.trick().isEmpty()) {
            for (int i = 0; i < 4; ++i) {
                if (playerHands[i].contains(Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    return PlayerId.values()[i];
                }
            }
        }
        return null;
    }
}
