package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.*;

import java.util.stream.IntStream;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public class HandBean {

    private ObservableList<Card> hand ;
    private ObservableSet<Card> playableCards ;

    public HandBean() {
        hand = FXCollections.observableArrayList();
        IntStream.range(0, HAND_SIZE).forEach(i -> hand.add(null));
    }

    public ObservableList<Card> handProperty() {
//        hand = FXCollections.observableArrayList();
//        IntStream.range(0, HAND_SIZE).forEach(i -> hand.add(null));
        return hand;
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
        return playableCards;
    }

    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        for (int i = 0; i < newPlayableCards.size(); i++) {
            playableCards.add(newPlayableCards.get(i));
        }
    }
}
