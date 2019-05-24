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
    private final Random shuffleRng;
    private final Random trumpRng;
    private final Map<PlayerId, String> playerNames;
    private final Map<PlayerId, CardSet> playerHands;
    private PlayerId firstPlayerTurn;
    private final int NUMBER_OF_PLAYERS = PlayerId.COUNT;

    /**
     * Constructs a Jass game with the given seed, players and player names.
     *
     * @param rngSeed the given seed.
     * @param players the given players.
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

        return turnState != null && ((turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS)
                || (turnState.score().totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS));

    }

    /**
     * Advance the game state until the end of the next trick, or do nothing if
     * the game is over.
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

    /*
     * Initializes a new turn with the given score.
     *
     * @param score the score of the previous turn or the initial score if
     * turnState is null.
     */
    private void newTurn(Score score) {

        shuffleAndDistribute();
        updateFirstPlayer();
        updateHand();
        Card.Color trump = Card.Color.ALL
                .get(trumpRng.nextInt(Card.Color.COUNT));
        setTrump(trump);

        turnState = TurnState.initial(trump, score, firstPlayerTurn);

    }

    /*
     * Sets the given trump as the one for the current turn.
     *
     * @param trump the given trump.
     */
    private void setTrump(Card.Color trump) {
        for (Player pl : players.values()) {
            pl.setTrump(trump);
        }
    }

    /*
     * Sets the winning team at the end of the game.
     */
    private void setWinningTeam() {
        for (Player pl : players.values()) {
            pl.setWinningTeam(winningTeam());
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
        for (int i = 0; i < NUMBER_OF_PLAYERS; ++i) {
            PlayerId pl = PlayerId.values()[i];
            playerHands.put(pl, CardSet.of(
                    deck.subList(i * HAND_SIZE, i * HAND_SIZE + HAND_SIZE)));
        }
    }

    /*
     * Makes the player who has to play in the trick play his card and update
     * the turnState with the card played.
     *
     */
    private void play() {
        Card card = nextPlayerCard();
        playerHands.put(turnState.nextPlayer(),
                playerHands.get(turnState.nextPlayer()).remove(card));
        players.get(turnState.nextPlayer())
                .updateHand(playerHands.get(turnState.nextPlayer()));
        turnState = turnState.withNewCardPlayed(card);

    }

    /*
     * Returns the card the player desires to play from the playable cards.
     *
     */
    private Card nextPlayerCard() {
        CardSet S = playerHands.get(turnState.nextPlayer());
        return players.get(turnState.nextPlayer()).cardToPlay(turnState, S);
    }

    private List<Card> constructCardList() {
        LinkedList<Card> list = new LinkedList<>();

        for (int i = 0; i < Card.Color.COUNT; i++) {
            for (int j = 0; j < Card.Rank.COUNT; ++j) {
                list.add(
                        Card.of(Card.Color.values()[i], Card.Rank.values()[j]));
            }
        }
        return list;
    }

    private TeamId winningTeam() {
        TeamId T1 = TeamId.TEAM_1;
        return turnState.score().totalPoints(T1) >= Jass.WINNING_POINTS ? T1
                : T1.other();

    }

    /*
     * Updates who the first player of the turn will be. So if the turnState is
     * null the first player will be the first player of the first turn , so the
     * one who has the seven of diamonds.
     */
    private void updateFirstPlayer() {
        if (firstPlayerTurn == null) {

            for (int i = 0; i < NUMBER_OF_PLAYERS; ++i) {
                PlayerId pl = PlayerId.values()[i];
                if (playerHands.get(pl).contains(
                        Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    firstPlayerTurn = pl;
                }
            }

        } else {
            firstPlayerTurn = PlayerId.values()[(firstPlayerTurn.ordinal() + 1)
                    % NUMBER_OF_PLAYERS];
        }
    }
}
