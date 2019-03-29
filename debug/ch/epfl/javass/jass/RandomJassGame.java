/*
 * Author :   Joseph E. Abboud.
 * Date   :   19 Mar 2019
 */

package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
//        Random rng = new Random(2019);
        for (PlayerId pId: PlayerId.ALL) {
          Player player = new RandomPlayer(2012);
          if (pId == PlayerId.PLAYER_1) {
              player = new PrintingPlayer(player);
          }
//          else {
//              player = new PacedPlayer(player, 1000);
//          }
          players.put(pId, player);
          playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(2019, players, playerNames);
      //  g.advanceToEndOfNextTrick();

        while (! g.isGameOver()) {
          g.advanceToEndOfNextTrick();
          System.out.println("----");
        }
      }
}
