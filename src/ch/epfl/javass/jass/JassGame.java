package ch.epfl.javass.jass;

import java.util.*;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;
import static java.util.Collections.unmodifiableMap;

public final class JassGame {

    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> playerHands;


    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {

        this.players = unmodifiableMap(new EnumMap<>(players));
        this.playerNames = unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        playerHands = unmodifiableMap(new EnumMap<>(playerHands));

    }

    public boolean isGameOver() {
        return ((turnState.score().gamePoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS) ||
                (turnState.score().gamePoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS)) ;
    }


    
    private void start(){
        shuffleAndDistribute();
        turnState = TurnState.initial(Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT)), Score.INITIAL, firstPlayer());
        firstPlayerToPlayTurn();
        ;
    }
    public void advanceToEndOfNextTrick() {
       
        if(isGameOver()) {
            
            return;
        }
        
        
        
        
        if(turnState== null) {
            // broadcaster les noms des joueurs
            turnState = TurnState.initial(Card.Color.ALL.get(trumpRng.nextInt(Card.Color.COUNT)), Score.INITIAL, firstPlayer());
            start(); // celui qui a le 7 de carreau commence 
        }
        else {
            turnState = turnState.withTrickCollected();
            if(turnState.isTerminal()) {
                newTurn(); // creeer un nouveau tour,redistribuer les cartes , choisir un nouveau  trump aleatoire, qui commence celui a droite du 7 de carreau
                
            }
            
        }
        
        // broedcaster le score et le pli a tous les joueurs 
        if(isGameOver()) {
            // boradcaster l equipe gagnante a tlm 
            return;
        }
        
        while(!turnState.trick().isFull()) {
           //quand il faut update trick 
            play(); //  chercher le prochain joueur ( avec next player) , cherche la carte que ce joueur joura . puis enlever cette carte de la main du joueur , puis mettre a jour 
            //cette main dans la map des mains 
            //puis apeller la methode withNewCardPlayed du turn State avec la carte en argument.
          //  player.get nextPlayer
        }
    }

    private void shuffleAndDistribute() {
        List<Card> deck = constructCardList();
        Collections.shuffle(deck, shuffleRng);
        for (int i = 0; i < 4; ++i){
          PlayerId pl = PlayerId.values()[i];
            players.get(pl).updateHand(CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8)));
            playerHands.put(pl, CardSet.of(deck.subList(i * HAND_SIZE, i * HAND_SIZE + 8)));
        }
    }

private void broadcastTrick() {

}
    private Card firstPlayerToPlayTurn() {
       return  players.get(firstPlayer()).cardToPlay(turnState, playerHands.get(firstPlayer()));

    }


    private Card nextPlayerToPlay() {
        return  players.get(turnState.nextPlayer()).cardToPlay(turnState, playerHands.get(turnState.nextPlayer()));

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
                PlayerId pl=PlayerId.values()[i];
                if (playerHands.get(pl). contains(Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    return pl;
                }
            }
        }
        return null;
    }
}
