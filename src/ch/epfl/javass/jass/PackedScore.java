package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

import static ch.epfl.javass.bits.Bits64.extract;
import static ch.epfl.javass.bits.Bits64.mask;
import static ch.epfl.javass.jass.Jass.MATCH_ADDITIONAL_POINTS;
import static ch.epfl.javass.jass.Jass.TRICKS_PER_TURN;
import static ch.epfl.javass.jass.TeamId.TEAM_1;
import static ch.epfl.javass.jass.TeamId.TEAM_2;

/**
 * Let one works with a Jass game score packed in a long type.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class PackedScore {

    /**
     * Represents the initial packed score when starting a game.
     */
    public static final long INITIAL = 0L;
    private static final int BIT_SHIFT_FOR_T2 = Integer.SIZE;
    private static final int NUMBER_OF_TRICKS_START = 0;
    private static final int NUMBER_OF_TRICKS_SIZE = 4;
    private static final int MAX_NUMBER_OF_TRICKS = 9;
    private static final int POINTS_OF_TURN_START = 4;
    private static final int POINTS_OF_TURN_SIZE = 9;
    private static final int MAX_POINTS_OF_TURN = 257;
    private static final int POINTS_OF_GAME_START = 13;
    private static final int POINTS_OF_GAME_SIZE = 11;
    private static final int MAX_POINTS_OF_GAME = 2000;
    private static final int INVALID_BITS_START = 24;
    private static final int INVALID_BITS_SIZE = 8;

    private PackedScore() {
    }

    /**
     * Returns true if the given packed score has a valid value.
     *
     * @param pkScore the packed score.
     * @return whether the score has a valid value.
     */
    public static boolean isValid(long pkScore) {
        boolean result = true;
        for (int i = 0; i <= 1; ++i) {

            long numberOfTricks =
                    extract(pkScore, NUMBER_OF_TRICKS_START + i * BIT_SHIFT_FOR_T2, NUMBER_OF_TRICKS_SIZE);
            long pointsOfTurn =
                    extract(pkScore, POINTS_OF_TURN_START + i * BIT_SHIFT_FOR_T2, POINTS_OF_TURN_SIZE);
            long pointsOfGame =
                    extract(pkScore, POINTS_OF_GAME_START + i * BIT_SHIFT_FOR_T2, POINTS_OF_GAME_SIZE);

            result &= (numberOfTricks >= 0) &&
                    (pointsOfTurn >= 0) &&
                    (pointsOfGame >= 0) &&

                    (numberOfTricks <= MAX_NUMBER_OF_TRICKS) &&
                    (pointsOfTurn <= MAX_POINTS_OF_TURN) &&
                    (pointsOfGame <= MAX_POINTS_OF_GAME) &&

                    (extract(pkScore, INVALID_BITS_START + i * 32, INVALID_BITS_SIZE) == 0);
        }
        return result;
    }

    /**
     * Packs the six components of a game score into a long variable.
     *
     * @param turnTricks1 the number of tricks won by team 1 during the current turn.
     * @param turnPoints1 the number of points won by team 1 during the current turn.
     * @param gamePoints1 the number of points won by team 1 during the last turns.
     * @param turnTricks2 the number of tricks won by team 2 during the current turn.
     * @param turnPoints2 the number of points won by team 2 during the current turn.
     * @param gamePoints2 the number of points won by team 2 during the last turns.
     * @return the packed score composed of the six components.
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
                            int turnTricks2, int turnPoints2, int gamePoints2) {

        int team1Score = Bits32.pack(turnTricks1, NUMBER_OF_TRICKS_SIZE, turnPoints1, POINTS_OF_TURN_SIZE,
                gamePoints1, POINTS_OF_GAME_SIZE);
        int team2Score = Bits32.pack(turnTricks2, NUMBER_OF_TRICKS_SIZE, turnPoints2, POINTS_OF_TURN_SIZE,
                gamePoints2, POINTS_OF_GAME_SIZE);
        return Bits64.pack(team1Score, BIT_SHIFT_FOR_T2, team2Score, BIT_SHIFT_FOR_T2);
    }

    /**
     * Returns the number of tricks won by the given team during the current turn, extracted from the packed score.
     *
     * @param pkScore the packed score.
     * @param t       the given team.
     * @return the number of tricks.
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return t.equals(TEAM_1) ? (int) extract(pkScore, NUMBER_OF_TRICKS_START, NUMBER_OF_TRICKS_SIZE) :
                (int) extract(pkScore, BIT_SHIFT_FOR_T2, NUMBER_OF_TRICKS_SIZE);

    }

    /**
     * Returns the number of points won by the given team during the current turn, extracted from the packed score.
     *
     * @param pkScore the packed score.
     * @param t       the given team.
     * @return the number of points.
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return t.equals(TEAM_1) ? (int) extract(pkScore, POINTS_OF_TURN_START, POINTS_OF_TURN_SIZE) :
                (int) extract(pkScore, POINTS_OF_TURN_START + BIT_SHIFT_FOR_T2, POINTS_OF_TURN_SIZE);
    }

    /**
     * Returns the number of points won by the given team during the last turns, extracted from the packed score.
     *
     * @param pkScore the packed score.
     * @param t       the given team.
     * @return the number of points.
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return t.equals(TEAM_1) ? (int) extract(pkScore, POINTS_OF_GAME_START, POINTS_OF_GAME_SIZE) :
                (int) extract(pkScore, POINTS_OF_GAME_START + BIT_SHIFT_FOR_T2, POINTS_OF_GAME_SIZE);
    }

    /**
     * Returns the total points won by the given team during the current game, extracted from the packed score.
     *
     * @param pkScore the packed score.
     * @param t       the given team.
     * @return the total number of points.
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return turnPoints(pkScore, t) + gamePoints(pkScore, t);
    }

    /**
     * Updates the packed score to take into consideration that the wining team won a trick worth "trickPoints".
     *
     * @param pkScore     the packed score to be updated.
     * @param winningTeam the winning team.
     * @param trickPoints the number of points of the trick.
     * @return the updated packed score.
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert (isValid(pkScore));
        if (winningTeam.equals(TEAM_1)) {
            long pkScoreUpdated = pkScore + 1L + (trickPoints << (long)NUMBER_OF_TRICKS_SIZE);
            if (turnTricks(pkScoreUpdated, TEAM_1) == TRICKS_PER_TURN) {
                pkScoreUpdated += (long) MATCH_ADDITIONAL_POINTS << (long)NUMBER_OF_TRICKS_SIZE;
            }
            return pkScoreUpdated;
        } else {
            long pkScoreUpdated = pkScore + (1L << (long)BIT_SHIFT_FOR_T2) +
                    ((long) trickPoints << (long)NUMBER_OF_TRICKS_SIZE + BIT_SHIFT_FOR_T2);
            if (turnTricks(pkScoreUpdated, TEAM_2) == TRICKS_PER_TURN) {
                pkScoreUpdated += (long) MATCH_ADDITIONAL_POINTS << (long)NUMBER_OF_TRICKS_SIZE + BIT_SHIFT_FOR_T2;
            }
            return pkScoreUpdated;
        }
    }

    /**
     * Updates the packed score to be ready for the next turn.
     * For each team, it adds their points of the current turn to their respective total points,
     * and set the value of the number of tricks and number of points for the current turn to zero.
     *
     * @param pkScore the packed score to be updated.
     * @return the updated packed score.
     */
    public static long nextTurn(long pkScore) {
        assert (isValid(pkScore));
        long extractedGamePoints1 = turnPoints(pkScore, TEAM_1);
        long extractedGamePoints2 = turnPoints(pkScore, TEAM_2);
        long pkScoreUpdated = pkScore & (mask(POINTS_OF_GAME_START, POINTS_OF_GAME_SIZE + INVALID_BITS_SIZE) |
                mask(POINTS_OF_GAME_START + BIT_SHIFT_FOR_T2, POINTS_OF_GAME_SIZE + INVALID_BITS_SIZE));
        pkScoreUpdated += extractedGamePoints1 << (long) POINTS_OF_GAME_START;
        pkScoreUpdated += extractedGamePoints2 << (long) POINTS_OF_GAME_START + BIT_SHIFT_FOR_T2;

        return pkScoreUpdated;
    }

    /**
     * Returns the written interpretation of the score.
     *
     * @param pkScore the packed score.
     * @return the written interpretation.
     */
    public static String toString(long pkScore) {
        assert (isValid(pkScore));
        return ("(" + turnTricks(pkScore, TEAM_1) + "," + turnPoints(pkScore, TEAM_1) +
                "," + gamePoints(pkScore, TEAM_1) + ")/(" + turnTricks(pkScore, TEAM_2) +
                "," + turnPoints(pkScore, TEAM_2) + "," + gamePoints(pkScore, TEAM_2) + ")");
    }
}
