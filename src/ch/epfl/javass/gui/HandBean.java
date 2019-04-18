package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public class HandBean {

    private ObservableList<Card> hand;
    private ObservableSet<Card> playableCards;

    public HandBean() {


    }

    public ObservableList<Card> hand() {
        return hand;
    }

    public void setHand(CardSet newHand) {
        if (newHand.size() != HAND_SIZE) {
            for (int i = 0; i < hand.size(); i++) {
                if (!newHand.contains(hand.get(i)) && hand.get(i) != null) {
                    hand.set(i, null);
                }
            }
        } else {
            for (int i = 0; i < hand.size(); i++) {
                hand.set(i, newHand.get(i));
            }
        }
    }

    public ObservableSet<Card> playableCards() {
        return playableCards;
    }

    public void setPlayableCards(CardSet newPlayableCards) {

    }
}
