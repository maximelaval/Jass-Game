package ch.epfl.javass.jass;

import java.util.ArrayList;

import java.util.List;
import java.util.SplittableRandom;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;
import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Represents a player simulated by the MCTS algorithm..
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class MctsPlayer implements Player {
    private final SplittableRandom mctsRng;
    private final PlayerId ownId;
    private final int iterations;


    /**
     * Constructs a MCTS player with the given seed, number of iterations of
     * turns and identity.
     *
     * @param ownId
     *            the given identity.
     * @param rngSeed
     *            the given seed.
     * @param iterations
     *            the given number of iterations of turns that the player will
     *            simulate.
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        checkArgument(iterations >= HAND_SIZE);

        mctsRng = new SplittableRandom(rngSeed);

        this.iterations = iterations;
       this.ownId = ownId;

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {


        CardSet playableCards = state.trick().playableCards(hand);
        long pkHand = playableCards.packed();

        if (playableCards.size() == 1) {
            return playableCards.get(0);
        } else {

            Node root = new Node(state, pkHand);
            for (int i = 0; i < iterations; ++i) {

                List<Node> path = promisingPath(root, hand.packed());

                Node childNode = path.get(path.size() - 1);
                Score generatedScore = scoreOfRandomTurnState(hand.packed(),childNode.turnState,ownId);

                long pkScore = generatedScore.packed();
                TeamId actualTeam = ownId.team().other();
                for (Node n : path) {


                    n.finishedRandomTurns += 1;
                    n.points += PackedScore.turnPoints(pkScore, actualTeam);
                    if (n.isNotTerminal()) {
                        actualTeam = n.turnState.nextPlayer().team();
                    }
                }

            }

            int pkCard = PackedCardSet.get(pkHand, root.promisingNodeIndex(0));
            return Card.ofPacked(pkCard);
        }
    }

    private List<Node> promisingPath(Node root, long mctsPlayerHand) {
         int CONSTANT_V = 40;
        List<Node> p = new ArrayList<>();
        Node n = root;
        p.add(root);
        while (n.isNotTerminal() && n.isFullyExpanded()) {

            n = n.bestChild(CONSTANT_V);
            p.add(n);
        }
        if (n.isNotTerminal()) {

            n = n.addChild(mctsPlayerHand,ownId);
            p.add(n);
        }
        return p;
    }
    private static long potentialCards(long mctsPlayerHand, PlayerId mctsOwnId,
                                TurnState turnState) {

        if(turnState.isTerminal()) return PackedCardSet.EMPTY;
        long playableCardsMcts = PackedTrick.playableCards(
                turnState.packedTrick(), PackedCardSet.intersection(
                        turnState.packedUnplayedCards(), mctsPlayerHand));
        long playableCardsOther = PackedTrick.playableCards(
                turnState.packedTrick(), PackedCardSet.difference(
                        turnState.packedUnplayedCards(), mctsPlayerHand));

        return (mctsOwnId== turnState.nextPlayer()) ? playableCardsMcts : playableCardsOther;

    }

    /*
     * simulates the end of a turn from a given turnState and return the
     * score obtained at the end of this turnState simulated.
     */
    private Score scoreOfRandomTurnState(long mctsPlayerHand,TurnState turnState,PlayerId ownId) {
        if (turnState.isTerminal()) return turnState.score();
        TurnState turnStateSimulated =TurnState.ofPackedComponents(
                turnState.packedScore(), turnState.unplayedCards().packed(),
                turnState.packedTrick()
        );


        int size ;
        while (!turnStateSimulated.isTerminal()) {
            long pkCardSet = potentialCards(mctsPlayerHand,ownId
                    , turnStateSimulated);
            size = PackedCardSet.size(pkCardSet);
            int pkCardRandom = PackedCardSet.get(pkCardSet,
                    mctsRng.nextInt(size));

            turnStateSimulated = turnStateSimulated
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(pkCardRandom));

        }

        return turnStateSimulated.score();
    }
    private static class Node {

        private final TurnState turnState;
        private final Node[] children;
        private Node parent;
        private long upcomingChildrenCards;
        private int points;
        private int finishedRandomTurns;

        /*
         * Constructs a Node with the given turnState and the given
         * upcomingChildrenCard which are the cards that the node can play and
         * can expand.
         */
        private Node(TurnState turnState, long upcomingChildrenCards) {
            this.turnState = turnState;
            this.children = new Node[PackedCardSet.size(upcomingChildrenCards)];
            this.upcomingChildrenCards = upcomingChildrenCards;
            finishedRandomTurns = 0;
            points = 0;

        }

        /*
         * returns the potential cards that the player that has to play can
         * (depending on where we are in the turnState can) play
         */


        /*
         * return the index of the most promising child node of the node to
         * which we apply this method.
         */
        private int promisingNodeIndex(int c) {
            int index = 0;
            double v = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i].V(c) > v) {
                    v = children[i].V(c);
                    index = i;
                }
            }
            return index;
        }

        private Node bestChild(int c) {
            return children[promisingNodeIndex(c)];
        }

        /*
         * adds a child node in the array children.
         */
        private Node addChild(long mctsPlayerHand,PlayerId ownId) {

            int promisingCard = PackedCardSet.get(upcomingChildrenCards, 0);
            TurnState turnStateChildren = turnState
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(promisingCard));
            Node childrenNode;
if (!turnStateChildren.isTerminal()) {
    long cardsToDevelop = potentialCards(mctsPlayerHand,
            ownId, turnStateChildren);


     childrenNode = new Node(turnStateChildren, cardsToDevelop);
} else{
     childrenNode = new Node(turnStateChildren, PackedCardSet.EMPTY);
}
            children[children.length
                    - PackedCardSet.size(upcomingChildrenCards)] = childrenNode;
            upcomingChildrenCards = PackedCardSet.remove(upcomingChildrenCards,
                    promisingCard);
            childrenNode.parent = this;

            return childrenNode;
        }

        private boolean isNotTerminal() {
            return !turnState.isTerminal(); //
        }

        private boolean isFullyExpanded() {
            return PackedCardSet.isEmpty(upcomingChildrenCards);
        }

        private double V(double constant) {
            assert (finishedRandomTurns >= 0);
            return finishedRandomTurns > 0
                    ? ((double) points / finishedRandomTurns + constant
                    * Math.sqrt(2 * Math.log(parent.finishedRandomTurns)
                    / (double) finishedRandomTurns))
                    : Double.POSITIVE_INFINITY;

        }

    }

}
