package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.IntStream;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public class HandBean {

    private final  ObservableList<Card> hand =FXCollections.observableArrayList(Collections.nCopies(9,null)) ;
    private final  ObservableSet<Card> playableCards =FXCollections.observableSet(new HashSet<>()) ;



    public ObservableList<Card> handProperty() {

        return FXCollections.unmodifiableObservableList(hand);
    }

    public void setHand(CardSet newHand) {

        if (newHand.size() != HAND_SIZE) {
            for (int i = 0; i < HAND_SIZE; i++) {
                if ( hand.get(i) != null && !newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }
            }
        } else {
            for (int i = 0; i < HAND_SIZE; i++) {
                hand.set(i, newHand.get(i));
            }
        }
    }

    public ObservableSet<Card> playableCardsProperty() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    public void setPlayableCards(CardSet newPlayableCards) {

        playableCards.clear();
        for (int i = 0; i < newPlayableCards.size(); i++) {
            playableCards.add(newPlayableCards.get(i));
        }
    }
}
