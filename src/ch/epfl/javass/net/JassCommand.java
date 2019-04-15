package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum JassCommand {
    PLRS(),
    TRMP(),
    HAND(),
    TRCK(),
    CARD(),
    SCOR(),
    WINR();


    public static final int COUNT = 7;
    public static final List<JassCommand> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    JassCommand() {

    }

}
