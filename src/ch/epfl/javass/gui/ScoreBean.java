package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.*;

/**
 * Represents a Java Bean that contains the current score of the game.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class ScoreBean {

    private final IntegerProperty turnPointsT1 = new SimpleIntegerProperty();
    private final IntegerProperty turnPointsT2 = new SimpleIntegerProperty();

    private final IntegerProperty gamePointsT1 = new SimpleIntegerProperty();
    private final IntegerProperty gamePointsT2 = new SimpleIntegerProperty();

    private final IntegerProperty totalPointsT1 = new SimpleIntegerProperty();
    private final IntegerProperty totalPointsT2 = new SimpleIntegerProperty();

    private final ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();


    /**
     * Returns the bean property of the turn points of the given team.
     *
     * @param team the given team.
     * @return the bean property.
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {

        return team.equals(TeamId.TEAM_1) ? turnPointsT1 : turnPointsT2;
    }

    /**
     * Sets the bean property turn points of the given team with the given new value.
     *
     * @param team          the given team.
     * @param newTurnPoints the given new value of the turn points.
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {

        if (team.equals(TeamId.TEAM_1)) {
            turnPointsT1.set(newTurnPoints);
        } else {
            turnPointsT2.set(newTurnPoints);
        }
    }

    /**
     * Returns the bean property of the game points of the given team.
     *
     * @param team the given team.
     * @return the bean property of the game points.
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team.equals(TeamId.TEAM_1) ? gamePointsT1 : gamePointsT2;
    }

    /**
     * Sets the bean property of the game points of the given team to the given new value.
     *
     * @param team          the given team.
     * @param newGamePoints the given new value.
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team.equals(TeamId.TEAM_1)) {
            gamePointsT1.set(newGamePoints);
        } else {
            gamePointsT2.set(newGamePoints);
        }
    }

    /**
     * Returns the bean property of the total points of the given team.
     *
     * @param team the given team.
     * @return the bean property of the total points.
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team.equals(TeamId.TEAM_1) ? totalPointsT1 : totalPointsT2;
    }

    /**
     * Sets the bean property of the total points of the given team to the given new value.
     *
     * @param team           the given team.
     * @param newTotalPoints the given new value.
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            totalPointsT1.set(newTotalPoints);
        } else {
            totalPointsT2.set(newTotalPoints);
        }
    }

    /**
     * Returns the bean property of the winning team.
     *
     * @return the bean property of the winning team.
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Sets the bean property of the winning team with the given team.
     *
     * @param winningTeam the given team.
     */
    public void setWinnigTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }

}
