package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.stream.IntStream;

public final class TrickBean {

    private SimpleObjectProperty trump;
    private ObservableMap<PlayerId, Card> trick;
    private SimpleObjectProperty winningPlayer;

    public TrickBean(Trick trick) {
        if (trick.isEmpty()) {
            winningPlayer = null;
        }
        trump.set(trick.trump());
        this.trick = FXCollections.observableHashMap();
        for (int i = 0; i < trick.size(); i++) {
            this.trick.put(trick.player(i), trick.card(i));
        }
        // GERER le cas lorsque tout le monde n'a pas encore jpoue du coup la map nest pas complete ??????????
    }

    public ReadOnlyObjectProperty trumpProperty() {
        return trump;
    }

    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    public void setTrick(Trick newTrick) {
        if (newTrick.isEmpty()) {
            winningPlayer = null;
        } else {
            for (int i = 0; i < trick.size(); i++) {
                this.trick.put(newTrick.player(i), newTrick.card(i));
            }
            // GERER le cas lorsque tout le monde n'a pas encore jpoue du coup la map nest pas complete ??????????
            winningPlayer.set(newTrick.winningPlayer());
        }
    }

    public ReadOnlyObjectProperty winningPlayerProperty() {
        return winningPlayer;
    }


}


