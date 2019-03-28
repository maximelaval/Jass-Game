package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the identity (the number) of a player.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    /**
     * Represents all the different players.
     */
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 4;

    /**
     * Returns the team of the given player.
     *
     * @return the team of the given player.
     */
    public TeamId team() {
        if (this.equals(PLAYER_1) || this.equals(PLAYER_3)) {
            return TeamId.TEAM_1;
        } else {
            return TeamId.TEAM_2;
        }
    }
}
