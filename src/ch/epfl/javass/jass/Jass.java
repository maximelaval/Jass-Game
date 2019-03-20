package ch.epfl.javass.jass;


/**
 * Contains some important constants.
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public interface Jass {

    /**
     * The size of a hand.
     */
    public static final int HAND_SIZE = 9;

    /**
     * The number of trick in one turn.
     */
    public static final int TRICKS_PER_TURN = 9;

    /**
     * The number of points required to win.
     */
    public static final int WINNING_POINTS = 1000;

    /**
     * Additional points in case a team win all the tricks during a turn.
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;

    /**
     * The points worth by the last trick of a turn.
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;



}
