package ch.epfl.javass.jass;


import java.util.SplittableRandom;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public final class MctsPlayer implements Player  {

    private static class Node{

        private TurnState turnState;
        private Node[] children;
        private Node parent;
        private CardSet upcomingChildren;
        private int points;
        private int finishedRandomTurns;

        private Node(TurnState turnState) {

        }



        private double UCT(int points, double constant){
            assert(finishedRandomTurns >= 0);
            if (finishedRandomTurns > 0) {
                return (points / finishedRandomTurns + constant * (Math.sqrt((2 * Math.log(finishedRandomTurns)) / parent.finishedRandomTurns)));
            } else {
                return Double.MAX_VALUE;
            }
        }


    }

    private final SplittableRandom mctsRng;

    MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if (iterations < HAND_SIZE)
            throw new IllegalArgumentException();
        else {
            SplittableRandom rng = new SplittableRandom(rngSeed);
            this.mctsRng = new SplittableRandom(rng.nextLong());

        }
    }



    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // creer pleins de methodes privées

        return null;
    }


}
