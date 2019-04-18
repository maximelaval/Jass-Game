package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;

public final class TrickBean {

    private ObservableMap trump;
    private SimpleObjectProperty trick;

    public TrickBean() {
        trump = new
        trick = new SimpleObjectProperty();
    }


    public ReadOnlyObjectProperty trumpProperty(Card.Color color) {

    }

    public ObservableMap<PlayerId, Card> trickProperty() {

    }

    void setTrick(Trick newTrick) {

    }



}


