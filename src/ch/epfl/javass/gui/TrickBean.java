package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;

public class TrickBean {

    private SimpleObjectProperty trump;
    private SimpleObjectProperty trick;


    public ReadOnlyObjectProperty trumpProperty(Card.Color color) {

    }

    public ObservableMap<PlayerId, Card> trickProperty() {

    }

    void setTrick(Trick newTrick) {

    }



}


