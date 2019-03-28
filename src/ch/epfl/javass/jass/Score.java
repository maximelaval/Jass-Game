package ch.epfl.javass.jass;

/**
 * Represents the scores of a Jass game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class Score {

    /**
     * Represents the initial score at the beginning of a game.
     */
    public static final Score INITIAL = new Score(0);

    private final long pkScore;

    private Score(long i) {
        this.pkScore = i;
    }

    /**
     * Returns the score corresponding to the packed score.
     *
     * @param packed the packed score.
     * @return the score corresponding to the packed score.
     */
    public static Score ofPacked(long packed) {
        if (PackedScore.isValid(packed)) {
            return new Score(packed);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the packed score version of the score.
     *
     * @return the packed score version of the score.
     */
    public long packed() {
        return pkScore;
    }

    /**
     * Returns the number of tricks won by the given team during the current turn, extracted from the score.
     *
     * @param t the given team.
     * @return the number of tricks.
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(pkScore, t);
    }

    /**
     * Returns the number of points won by the given team during the current turn, extracted from the score.
     *
     * @param t the given team.
     * @return the number of points.
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(pkScore, t);
    }

    /**
     * Returns the number of points won by the given team during the last turns, extracted from the score.
     *
     * @param t the given team.
     * @return the number of points.
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(pkScore, t);
    }

    /**
     * Returns the total points won by the given team during the current game, extracted from the score.
     *
     * @param t the given team.
     * @return the total number of points.
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(pkScore, t);
    }

    /**
     * Updates the score to take into consideration that the wining team won a trick worth "trickPoints".
     *
     * @param winningTeam the winning team.
     * @param trickpoints the number of points of the tricks.
     * @return the updated score.
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickpoints) {
        if (trickpoints < 0) {
            throw new IllegalArgumentException();
        } else {
            return new Score(PackedScore.withAdditionalTrick(pkScore, winningTeam, trickpoints));
        }
    }

    /**
     * Updates the score to be ready for the next turn.
     *
     * @return the updated score.
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(pkScore));
    }


    @Override
    public boolean equals(Object that0) {
        return (!(that0 == null)) && (this.hashCode() == that0.hashCode()) && (that0.getClass().equals(this.getClass())) && (((Score) that0).packed() == pkScore);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(pkScore);
    }

    @Override
    public String toString() {
        return PackedScore.toString(pkScore);
    }
}