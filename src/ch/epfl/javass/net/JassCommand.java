package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the commands sent through the network by the game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public enum JassCommand {
    PLRS(),
    TRMP(),
    HAND(),
    TRCK(),
    CARD(),
    SCOR(),
    WINR();

    /**
     * Represents the total number of commands.
     */
    public static final int COUNT = 7;
    /**
     * Represents all different commands.
     */
    public static final List<JassCommand> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    JassCommand() {

    }

}
