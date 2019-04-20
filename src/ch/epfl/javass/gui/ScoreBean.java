package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ScoreBean {

    private SimpleIntegerProperty turnPointsT1;
    private SimpleIntegerProperty turnPointsT2;

    private SimpleIntegerProperty gamePointsT1;
    private SimpleIntegerProperty gamePointsT2;

    private SimpleIntegerProperty totalPointsT1;
    private SimpleIntegerProperty totalPointsT2;

    private SimpleObjectProperty winningTeam;


    private ScoreBean() {
        turnPointsT1 = new SimpleIntegerProperty();
        turnPointsT2 = new SimpleIntegerProperty();
        gamePointsT1 = new SimpleIntegerProperty();
        gamePointsT2 = new SimpleIntegerProperty();
        totalPointsT1 = new SimpleIntegerProperty();
        totalPointsT2 = new SimpleIntegerProperty();
        winningTeam = new SimpleObjectProperty();
    }

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team.equals(TeamId.TEAM_1) ? turnPointsT1 : turnPointsT2;
    }

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            turnPointsT1.set(newTurnPoints);
        } else {
            turnPointsT2.set(newTurnPoints);
        }
    }

    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team.equals(TeamId.TEAM_1) ? gamePointsT1 : gamePointsT2;
    }

    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team.equals(TeamId.TEAM_1)) {
            gamePointsT1.set(newGamePoints);
        } else {
            gamePointsT2.set(newGamePoints);
        }
    }

    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team.equals(TeamId.TEAM_1) ? totalPointsT1 : totalPointsT2;
    }

    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1)) {
            totalPointsT1.set(newTotalPoints);
        } else {
            totalPointsT2.set(newTotalPoints);
        }
    }

    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;     //???????????????????????
    }

    public void setWinnigTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }

}
