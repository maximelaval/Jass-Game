package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

public final class ScoreBean {

    private IntegerProperty turnPointsT1 =  new SimpleIntegerProperty()  ;
    private IntegerProperty turnPointsT2 = new SimpleIntegerProperty();

    private IntegerProperty gamePointsT1 = new SimpleIntegerProperty();
    private IntegerProperty gamePointsT2 = new SimpleIntegerProperty() ;

    private IntegerProperty totalPointsT1 = new SimpleIntegerProperty();
    private IntegerProperty totalPointsT2 = new SimpleIntegerProperty();

    private ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();



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
        return winningTeam;
    }

    public void setWinnigTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }

}
