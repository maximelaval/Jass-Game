package ch.epfl.javass.jass;

import ch.epfl.javass.gui.HandBean;
import javafx.collections.ListChangeListener;

public class main {

    public static void main(String[] args) {
        HandBean hb = new HandBean();
        ListChangeListener<Card> listener = e -> System.out.println(e);
        hb.handProperty().addListener(listener);

        CardSet h = CardSet.EMPTY
                .add(Card.of(Card.Color.SPADE, Card.Rank.SIX))
                .add(Card.of(Card.Color.SPADE, Card.Rank.NINE))
                .add(Card.of(Card.Color.SPADE, Card.Rank.JACK))
                .add(Card.of(Card.Color.HEART, Card.Rank.SEVEN))
                .add(Card.of(Card.Color.HEART, Card.Rank.ACE))
                .add(Card.of(Card.Color.DIAMOND, Card.Rank.KING))
                .add(Card.of(Card.Color.DIAMOND, Card.Rank.ACE))
                .add(Card.of(Card.Color.CLUB, Card.Rank.TEN))
                .add(Card.of(Card.Color.CLUB, Card.Rank.QUEEN));
        hb.setHand(h);
        while (!h.isEmpty()) {
            h = h.remove(h.get(0));
            hb.setHand(h);
        }
    }
}
