package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ScoreBean {

    private SimpleIntegerProperty turnPoints;
    private SimpleIntegerProperty gamePoints;
    private SimpleIntegerProperty totalPoints;
    private SimpleObjectProperty winnigTeam;


    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {

    }

    public void setTurnPoints(TeamId team, int newTurnPoints) {

    }

    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {

    }

    public void setGamePoints(TeamId team) {

    }

    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {

    }

    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {

    }

    public void setWinnigTeam(TeamId winningTeam) {

    }

}
