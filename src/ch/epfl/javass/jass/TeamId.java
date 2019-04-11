package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines a team for the game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public enum TeamId {
    TEAM_1,
    TEAM_2;

    /**
     * Represents all different teams.
     */
    public static final List<TeamId> ALL = Collections.unmodifiableList((Arrays.asList(values())));

    /**
     * The number of different teams.
     */
    public static final int COUNT = 2;

    /**
     * Returns the opposite team of the given team.
     *
     * @return the opposite team of the given team.
     */
    public TeamId other() {
        return this.equals(TEAM_1) ? TEAM_2 : TEAM_1;
    }
}
