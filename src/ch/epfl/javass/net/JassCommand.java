package ch.epfl.javass.net;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum JassCommand {
    PLRS("setPlayers"),
    TRMP("setTrump"),
    HAND("updateHand"),
    TRCK("updateTrick"),
    CARD("cardToPlay"),
    SCOR("updateScore"),
    WINR("setWinningTeam");


    public static final int COUNT = 7;
    public static final List<JassCommand> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    private final String methodName;

    JassCommand(String methodName) {
        this.methodName = methodName;
    }

}
