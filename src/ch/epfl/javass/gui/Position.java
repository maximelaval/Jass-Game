package ch.epfl.javass.gui;

import ch.epfl.javass.jass.PlayerId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Position {
    BOTTOM(),
    RIGHT(),
    TOP(),
    LEFT;

    public static final List<Position> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 4;

}
