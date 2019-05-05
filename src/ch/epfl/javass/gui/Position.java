package ch.epfl.javass.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines the position of a player on the screen.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public enum Position {
    BOTTOM(),
    RIGHT(),
    TOP(),
    LEFT;

    /**
     * Represents all different positions.
     */
    public static final List<Position> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    /**
     * The number of different positions.
     */
    public static final int COUNT = 4;
}
