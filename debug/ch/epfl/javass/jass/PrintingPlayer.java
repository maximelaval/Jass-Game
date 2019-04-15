/*
 * Author :   Joseph E. Abboud.
 * Date   :   18 Mar 2019
 */

package ch.epfl.javass.jass;

import java.util.Map;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.println("Cartes jouables de ma main : " + state.trick().playableCards(hand));
        System.out.print("C'est à moi de jouer... Je joue : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c);        

        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("Les joueurs sont : ");
        for (Map.Entry<PlayerId, String> entry : playerNames.entrySet()) {
            if (entry.getKey().equals(ownId)) {
                System.out.println(entry.getValue() + " (moi)");
            } else {
                System.out.println(entry.getValue());
            }
        }
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main : " + newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        System.out.println("Atout : " + trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Pli " + newTrick.index() + ", commencé par " + newTrick.player(0) + " : " + newTrick);
    }

    @Override
    public void updateScore(Score score) {
        System.out.println("Scores: " + score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("L'équipe gagnante est : " + winningTeam);
        System.out.println("--------------------Set Winning Team was called----------------------------");

    }
}
