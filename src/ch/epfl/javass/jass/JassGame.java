package ch.epfl.javass.jass;

import java.util.*;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;
import static java.util.Collections.unmodifiableMap;

/**
 * Represents a Jass game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class JassGame {

    private final Map<PlayerId, Player> players;
    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> playerHands;
    private PlayerId firstPlayerTurn;

    /**
     * Constructs a Jass game with the given seed, players and player names.
     *
     * @param rngSeed     the given seed.
     * @param players     the given players.
     * @param playerNames the given player names.
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
                    Map<PlayerId, String> playerNames) {

        this.players = unmodifiableMap(new EnumMap<>(players));
        this.playerNames = unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.playerHands = new HashMap<>();

    }

    /**
     * Returns true if and only if the game is over.
     *
     * @return true if and only if the game is over.
     */
    public boolean isGameOver() {
        if (turnState != null) {
            return ((turnState.score()
                    .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS)
                    || (turnState.score().totalPoints(
                    TeamId.TEAM_2) >= Jass.WINNING_POINTS));
        } else {
            return false;
        }

    }

    /**
     * Initializes a new turn with the score of the previous turn..
     *
     * @param score the score of the previous turn.
     */
    private void newTurn(Score score) {
        // assert (turnState.isTerminal() || turnState == null);

        shuffleAndDistribute();
        updateFirstPlayer();
        updateHand();
        Card.Color trump = Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT));
        setTrump(trump);

        turnState = TurnState.initial(
                trump, score,
                firstPlayerTurn);
//        playerHands.put(firstPlayerTurn,
//                playerHands.get(firstPlayerTurn).remove(firstPlayerCard()));
//        players.get(firstPlayerTurn)
//                .updateHand(playerHands.get(firstPlayerTurn));
        // turnState= turnState.withNewCardPlayed(firstPlayerCard());
        // updateTrick();

    }

    /**
     * Sets the given trump as the one for the current turn.
     *
     * @param trump the given trump.
     */
    private void setTrump(Card.Color trump) {
        for (Player pl : players.values()) {
            pl.setTrump(trump);
        }
    }

    /**
     * Sets the winning team at the end of the game.
     */
    private void setWinningTeam() {
        for (Player pl : players.values()) {
            pl.setWinningTeam(winningTeam());
        }
    }

    /**
     * Advance the game state until the end of the next trick, or do nothing if the game is over.
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {

            return;
        }

        if (turnState == null) {
            for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
                entry.getValue().setPlayers(entry.getKey(), playerNames);
            }

            newTurn(Score.INITIAL);
        } else {
            turnState = turnState.withTrickCollected();
            if (turnState.isTerminal()) {
                newTurn(turnState.score().nextTurn());

            }
        }

        updateScore();
        updateTrick();

        if (isGameOver()) {
            setWinningTeam();
            return;
        }

        while (!PackedTrick.isFull(turnState.packedTrick())) {

            play();
            updateTrick();
        }
    }

    private void updateHand() {
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            entry.getValue().updateHand(playerHands.get(entry.getKey()));

        }
    }

    private void updateTrick() {
        for (Player pl : players.values()) {
            pl.updateTrick(turnState.trick());

        }
    }

    private void updateScore() {
        for (Player pl : players.values()) {
            pl.updateScore(turnState.score());
        }
    }

    private void shuffleAndDistribute() {
        List<Card> deck = constructCardList();
        Collections.shuffle(deck, shuffleRng);
        for (int i = 0; i < 4; ++i) {
            PlayerId pl = PlayerId.values()[i];
            // players.get(pl).updateHand(CardSet.of(deck.subList(i * HAND_SIZE,
            // i * HAND_SIZE + 8)));
            playerHands.put(pl, CardSet.of(
                    deck.subList(i * HAND_SIZE, i * HAND_SIZE + HAND_SIZE)));
        }
    }

    private void play() {
        Card card = nextPlayerCard();
        playerHands.put(turnState.nextPlayer(),
                playerHands.get(turnState.nextPlayer()).remove(card));
        players.get(turnState.nextPlayer())
                .updateHand(playerHands.get(turnState.nextPlayer()));

        turnState = turnState.withNewCardPlayed(card);


        if (turnState.packedTrick() == (PackedTrick.INVALID)) {

            return;
        }

        System.out.println(turnState.trick());


//        if (turnState.trick().isEmpty()) {
//            updateTrick();
//            updateScore();
//        }

    }

//    private Card firstPlayerCard() {
//        CardSet S = turnState.trick()
//                .playableCards(playerHands.get(firstPlayerTurn));
//        return players.get(firstPlayerTurn).cardToPlay(turnState, S);
//
//    }

    private Card nextPlayerCard() {
        CardSet S = turnState.trick()
                .playableCards(playerHands.get(turnState.nextPlayer()));
        return players.get(turnState.nextPlayer()).cardToPlay(turnState, S);
    }

    private List<Card> constructCardList() {
        LinkedList<Card> list = new LinkedList<Card>();

        for (int i = 0; i < Card.Color.COUNT; i++) {
            for (int j = 0; j < Card.Rank.COUNT; ++j) {
                list.add(
                        Card.of(Card.Color.values()[i], Card.Rank.values()[j]));
            }
        }
        return list;
    }

    private TeamId winningTeam() {
        // assert(isGameOver());
        TeamId T1 = TeamId.TEAM_1;
        if (turnState.score().totalPoints(T1) >= Jass.WINNING_POINTS) {
            return T1;
        } else {
            return T1.other();
        }

    }

    private void updateFirstPlayer() {
        if (firstPlayerTurn == null) {

            for (int i = 0; i < 4; ++i) {
                PlayerId pl = PlayerId.values()[i];
                if (playerHands.get(pl).contains(
                        Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    firstPlayerTurn = pl;
                }
            }

        } else {
            firstPlayerTurn = PlayerId.values()[(firstPlayerTurn.ordinal() + 1)
                    % 4];
        }
    }
}