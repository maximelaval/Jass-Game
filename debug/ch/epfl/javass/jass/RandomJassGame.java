/*
 * Author :   Joseph E. Abboud.
 * Date   :   19 Mar 2019
 */

package ch.epfl.javass.jass;

import ch.epfl.javass.gui.HandBean;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.net.JassCommand;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.collections.ListChangeListener;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public final class RandomJassGame {
    public static void main(String[] args) {


        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
//        Random rng = new Random(2019);
        for (PlayerId pId : PlayerId.ALL) {
            Player player = new RandomPlayer(2012);
//            if (pId == PlayerId.PLAYER_1) {
//                player = new PrintingPlayer(player);
//            }
            if (pId == PlayerId.PLAYER_2) {
                player = new RemotePlayerClient("128.179.179.154");
            }
//          else {
//              player = new PacedPlayer(player, 1000);
//          }
            players.put(pId, player);
            //playerNames.put(pId, Integer.toBinaryString(pId.ordinal() + 1));
        }
        playerNames.put(PlayerId.PLAYER_1, "Lucas_Player1");
        playerNames.put(PlayerId.PLAYER_2, "Maxime_Player2");
        playerNames.put(PlayerId.PLAYER_3, "Kazuma_Player3");
        playerNames.put(PlayerId.PLAYER_4, "Alexy_Player4");
        JassGame g = new JassGame(2019, players, playerNames);


        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();

            // TESTS
            System.out.println(Card.Rank.valueOf("SIX"));
            System.out.println(JassCommand.valueOf("PLRS"));
            JassCommand jasscomm = JassCommand.valueOf("PLRS");
            System.out.println(Card.Rank.SIX.name());

            StringJoiner joiner = new StringJoiner(":");
            joiner.add("salut");
            joiner.add("comment");
            System.out.println(joiner.toString());


            //BEANS

            HandBean hb = new HandBean();
            ListChangeListener<Card> listener = e -> System.out.println(e);
            hb.handProperty().addListener(listener);

            CardSet h = CardSet.EMPTY
                    .add(Card.of(Color.SPADE, Rank.SIX))
                    .add(Card.of(Color.SPADE, Rank.NINE))
                    .add(Card.of(Color.SPADE, Rank.JACK))
                    .add(Card.of(Color.HEART, Rank.SEVEN))
                    .add(Card.of(Color.HEART, Rank.ACE))
                    .add(Card.of(Color.DIAMOND, Rank.KING))
                    .add(Card.of(Color.DIAMOND, Rank.ACE))
                    .add(Card.of(Color.CLUB, Rank.TEN))
                    .add(Card.of(Color.CLUB, Rank.QUEEN));
            hb.setHand(h);
            while (!h.isEmpty()) {
                h = h.remove(h.get(0));
                hb.setHand(h);
            }
            // TESTS

            //System.out.println("----");
            // }
        }
    }
}
