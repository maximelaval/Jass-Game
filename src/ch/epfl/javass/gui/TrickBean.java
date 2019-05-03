package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public final class TrickBean {

    private SimpleObjectProperty<Color> trump = new SimpleObjectProperty<>();
    private ObservableMap<PlayerId, Card> trick = FXCollections.observableHashMap();

    private ReadOnlyObjectProperty<PlayerId> winningPlayer;

    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    public void setTrick(Trick newTrick) {
        if (newTrick.isEmpty()) {
            winningPlayer = new SimpleObjectProperty<>(null);
        } else {
            for (int i = 0; i < newTrick.size(); i++) {
                this.trick.put(newTrick.player(i), newTrick.card(i));
            }
            winningPlayer = new SimpleObjectProperty<>(newTrick.winningPlayer());
        }
    }

    public void setTrump(Color trump) {
        this.trump.set(trump);
    }

    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }

}