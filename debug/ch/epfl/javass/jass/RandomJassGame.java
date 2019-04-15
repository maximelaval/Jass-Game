/*
 * Author :   Joseph E. Abboud.
 * Date   :   19 Mar 2019
 */

package ch.epfl.javass.jass;

import ch.epfl.javass.net.JassCommand;
import ch.epfl.javass.net.RemotePlayerClient;

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
                player = new RemotePlayerClient("localhost");
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
        //g.advanceToEndOfNextTrick();

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

            // TESTS

            //System.out.println("----");
        }
    }
}
