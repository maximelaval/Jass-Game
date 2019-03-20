package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import static ch.epfl.javass.bits.Bits64.extract;
import static ch.epfl.javass.bits.Bits64.mask;
import static ch.epfl.javass.jass.Jass.MATCH_ADDITIONAL_POINTS;
import static ch.epfl.javass.jass.Jass.TRICKS_PER_TURN;
import static ch.epfl.javass.jass.TeamId.*;

/**
 * Let one works with a Jass game score packed in a long type.
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class PackedScore {

    /**
     * Represents the initial packed score when starting a game.
     */
    public static final long INITIAL = 0L;

    private PackedScore() {
    }

    /**
     * Returns true if the given packed score has a valid value.
     * @param pkScore the packed score.
     * @return whether the score has a valid value.
     */
    public static boolean isValid(long pkScore) {
        if ((extract(pkScore, 0, 4) >= 0) && (extract(pkScore, 4, 9) >= 0) &&
                (extract(pkScore, 13, 11) >= 0) && (extract(pkScore, 0, 4) <= 9) &&
                (extract(pkScore, 4, 9) <= 257) && (extract(pkScore, 13, 11) <= 2000) &&
                (extract(pkScore, 24, 8) == 0) && (extract(pkScore, 32, 4) >= 0) &&
                (extract(pkScore, 36, 9) >= 0) && (extract(pkScore, 45, 11) >= 0) &&
                (extract(pkScore, 32, 4) <= 9) && (extract(pkScore, 36, 9) <= 257) &&
                (extract(pkScore, 45, 11) <= 2000) &&
                (extract(pkScore, 56, 8) == 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Packs the six components of a game score into a long variable.
     * @param turnTricks1 the number of tricks won by team 1 during the current turn.
     * @param turnPoints1 the number of points won by team 1 during the current turn.
     * @param gamePoints1 the number of points won by team 1 during the last turns.
     * @param turnTricks2  the number of tricks won by team 2 during the current turn.
     * @param turnPoints2 the number of points won by team 2 during the current turn.
     * @param gamePoints2 the number of points won by team 2 during the last turns.
     * @return  the packed score composed of the six components.
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
                            int turnTricks2, int turnPoints2, int gamePoints2) {

        int team1Score = Bits32.pack(turnTricks1, 4, turnPoints1, 9, gamePoints1, 11);
        int team2Score = Bits32.pack(turnTricks2, 4, turnPoints2, 9, gamePoints2, 11);
        return Bits64.pack(team1Score, 32, team2Score, 32);
    }

    /**
     * Returns the number of tricks won by the given team during the current turn, extracted from the packed score.
     * @param pkScore the packed score.
     * @param t the given team.
     * @return the number of tricks.
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if (t.equals(TEAM_1)) {
            return (int) extract(pkScore, 0, 4);
        } else {
            return (int) extract(pkScore, 32, 4);
        }
    }

    /**
     * Returns the number of points won by the given team during the current turn, extracted from the packed score.
     * @param pkScore the packed score.
     * @param t the given team.
     * @return the number of points.
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if (t.equals(TEAM_1)) {
            return (int) extract(pkScore, 4, 9);
        } else {
            return (int) extract(pkScore, 36, 9);
        }
    }

    /**
     * Returns the number of points won by the given team during the last turns, extracted from the packed score.
     * @param pkScore the packed score.
     * @param t the given team.
     * @return the number of points.
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        if (t.equals(TEAM_1)) {
            return (int) extract(pkScore, 13, 11);
        } else {
            return (int) extract(pkScore, 45, 11);
        }
    }

    /**
     * Returns the total points won by the given team during the current game, extracted from the packed score.
     * @param pkScore the packed score.
     * @param t the given team.
     * @return the total number of points.
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert(isValid(pkScore));
        return turnPoints(pkScore, t) + gamePoints(pkScore, t);
    }

    /**
     * Updates the packed score to take into consideration that the wining team won a trick worth "trickPoints".
     * @param pkScore the packed score to be updated.
     * @param winningTeam the winning team.
     * @param trickPoints the number of points of the trick.
     * @return the updated packed score.
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert(isValid(pkScore));
        if (winningTeam.equals(TEAM_1)) {
            long pkScoreUpdated = pkScore + 1L + (trickPoints << 4L);
            if (turnTricks(pkScoreUpdated, TEAM_1) == TRICKS_PER_TURN) {
                pkScoreUpdated += (long)MATCH_ADDITIONAL_POINTS << 4L;
            }
            return pkScoreUpdated;
        } else {
            long i = trickPoints;
            long pkScoreUpdated = pkScore + (1L << 32L) + (i << 36L);
            if (turnTricks(pkScoreUpdated, TEAM_2) == TRICKS_PER_TURN) {
                pkScoreUpdated += (long)MATCH_ADDITIONAL_POINTS << 36L;
            }
            return pkScoreUpdated;
        }
    }

    /**
     * Updates the packed score to be ready for the next turn.
     * For each team, it adds their points of the current turn to their respective total points,
     * and set the value of the number of tricks and number of points for the current turn to zero.
     * @param pkScore the packed score to be updated.
     * @return the updated packed score.
     */
    public static long nextTurn(long pkScore) {
        assert(isValid(pkScore));
        long extractedGamePoints1 = turnPoints(pkScore, TEAM_1);
        long extractedGamePoints2 =  turnPoints(pkScore, TEAM_2);
        long pkScoreUpdated = pkScore & (mask(13,19) | mask(45,19));
        pkScoreUpdated += extractedGamePoints1 << 13L;
        pkScoreUpdated += extractedGamePoints2 << 45L;

        return pkScoreUpdated;
    }

    /**
     * Returns the written interpretation of the score.
     * @param pkScore the packed score.
     * @return the written interpretation.
     */
    public static String toString(long pkScore) {
        assert(isValid(pkScore));
        return ("(" + turnTricks(pkScore, TEAM_1) + "," + turnPoints(pkScore, TEAM_1) + "," + gamePoints(pkScore, TEAM_1) +
                ")/(" + turnTricks(pkScore, TEAM_2) + "," + turnPoints(pkScore, TEAM_2) + "," + gamePoints(pkScore, TEAM_2) + ")");
    }
}
