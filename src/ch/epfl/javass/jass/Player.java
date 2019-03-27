package ch.epfl.javass.jass;

import java.util.Map;

/**
 * Represents a player.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public interface Player {

    /**
     * Returns the card the player desires to play.
     *
     * @param state the current state of the game.
     * @param hand  the card hand of the player that will play.
     * @return the card the player desires to play.
     */
    Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Informs the player that he or she has the "ownId" identity and informs the player about the names
     * of the other players. Is called once at the beginning of the game.
     *
     * @param ownId       the identity of the player.
     * @param playerNames the names of the players
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

    }

    /**
     * Informs the player about his or her new card hand. Is called every time the player's hand changes.
     *
     * @param newHand the new card hand of the player.
     */
    default void updateHand(CardSet newHand) {

    }

    /**
     * Informs the player about the trump card.
     *
     * @param trump the trump card.
     */
    default void setTrump(Card.Color trump) {

    }

    /**
     * Informs the player about a change in the trick. Is called every time the trick is modified.
     *
     * @param newTrick the updated trick.
     */
    default void updateTrick(Trick newTrick) {

    }

    /**
     * Informs the player about the updated score. Is called every time a trick is collected.
     *
     * @param score the updated score.
     */
    default void updateScore(Score score) {

    }

    /**
     * Set the winning team. Is called once as soon as a team reaches 1 000 points or more.
     *
     * @param winningTeam the winning team.
     */
    default void setWinningTeam(TeamId winningTeam) {

    }
}
