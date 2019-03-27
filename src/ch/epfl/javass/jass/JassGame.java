package ch.epfl.javass.jass;

import java.util.*;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;
import static java.util.Collections.unmodifiableMap;

import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.TeamId;;

public final class JassGame {

    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> playerHands;
    private PlayerId firstPlayerTurn;


    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {

        this.players = unmodifiableMap(new EnumMap<>(players));
        this.playerNames = unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.playerHands = new HashMap<PlayerId, CardSet>();

    }

    public boolean isGameOver() {
        //assert(turnState!=null);
        if (turnState != null) {
            return ((turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS) ||
                    (turnState.score().totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS));
        } else {
            return false;
        }
    }


    private void newTurn(Score score) {
        assert (turnState.isTerminal() || turnState == null);
        shuffleAndDistribute();
        updateFirstPlayer();
        updateHand();
        setTrump();
        turnState = TurnState.initial(Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT)), score, firstPlayerTurn);
        playerHands.put(firstPlayerTurn, playerHands.get(firstPlayerTurn).remove(firstPlayerCard()));
        players.get(firstPlayerTurn).updateHand(playerHands.get(firstPlayerTurn));
        turnState.withNewCardPlayed(firstPlayerCard());
        updateTrick();


    }

    private void setTrump() {
        for (Player pl : players.values()) {
            pl.setTrump(Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT)));
        }
    }

    private void setWinningTeam() {
        for (Player pl : players.values()) {
            pl.setWinningTeam(winningTeam());
        }
    }

    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {

            setWinningTeam();
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
                newTurn(turnState.score());

            }

        }

        if (isGameOver()) {
            setWinningTeam();
            return;
        }

        while (!turnState.trick().isFull()) {
            play();
        }
    }


    private void updateHand() {
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            entry.getValue().updateHand(playerHands.get(entry.getKey()));
            ;

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
            players.get(pl).updateHand(CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8)));
            playerHands.put(pl, CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8)));
        }
    }


    private void play() {
        playerHands.put(turnState.nextPlayer(), playerHands.get(turnState.nextPlayer()).remove(nextPlayerCard()));
        players.get(turnState.nextPlayer()).updateHand(playerHands.get(turnState.nextPlayer()));

        turnState.withNewCardPlayedAndTrickCollected(nextPlayerCard());
        if (turnState.trick().isEmpty()) {
            updateTrick();
            updateScore();
        }


    }

    private Card firstPlayerCard() {
        return players.get(firstPlayerTurn).cardToPlay(turnState, playerHands.get(firstPlayerTurn));

    }


    private Card nextPlayerCard() {
        return players.get(turnState.nextPlayer()).cardToPlay(turnState, playerHands.get(turnState.nextPlayer()));

    }


    private List<Card> constructCardList() {
        LinkedList<Card> list = new LinkedList<Card>();

        for (int i = 0; i < Card.Color.COUNT; i++) {
            for (int j = 0; j < Card.Rank.COUNT; j++) {
                list.add(Card.of(Card.Color.values()[i], Card.Rank.values()[j]));
            }
        }
        return list;
    }

    private TeamId winningTeam() {
        assert (isGameOver());
        TeamId T1 = TeamId.TEAM_1;
        if (turnState.score().totalPoints(T1) == Jass.WINNING_POINTS) {
            return T1;
        } else {
            return T1.other();
        }

    }


    private void updateFirstPlayer() {
        if (firstPlayerTurn == null) {

            for (int i = 0; i < 4; ++i) {
                PlayerId pl = PlayerId.values()[i];
                if (playerHands.get(pl).contains(Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    firstPlayerTurn = pl;
                }
            }

        } else {
            firstPlayerTurn = PlayerId.values()[(firstPlayerTurn.ordinal() + 1) % 4];
        }
    }
}