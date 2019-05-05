package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Represents a Java Bean that contains the current trick.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class TrickBean {

    private SimpleObjectProperty<Color> trump = new SimpleObjectProperty<>();
    private ObservableMap<PlayerId, Card> trick = FXCollections.observableHashMap();
    private ReadOnlyObjectProperty<PlayerId> winningPlayer;

    /**
     * Returns the bean property of the trump.
     *
     * @return the bean property of the trump.
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    /**
     * Returns the bean property of the trick.
     *
     * @return the bean property of the trick.
     */
    public ObservableMap<PlayerId, Card> trickProperty() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * Sets the bean property of the trick with the given new trick.
     *
     * @param newTrick the given new trick.
     */
    public void setTrick(Trick newTrick) {
        if (!this.trick.isEmpty()) {
            this.trick.clear();
        }
        if (newTrick.isEmpty()) {
            winningPlayer = new SimpleObjectProperty<>(null);
        } else {
            for (int i = 0; i < newTrick.size(); i++) {
                this.trick.put(newTrick.player(i), newTrick.card(i));
            }
            winningPlayer = new SimpleObjectProperty<>(newTrick.winningPlayer());
        }
    }

    /**
     * Sets the bean property of the trump with the given new trump.
     *
     * @param trump the given new trump.
     */
    public void setTrump(Color trump) {
        this.trump.set(trump);
    }

    /**
     * Returns the bean property of the winning player.
     *
     * @return the bean property of the winning player.
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }

}