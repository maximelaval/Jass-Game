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
    private static SplittableRandom mctsRng;
    private static PlayerId playerId;
    private int iterations;
    int CONSTANT_V = 40;

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
        playerId = ownId;

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        CardSet playableCards = state.trick().playableCards(hand);
        long pkHand = playableCards.packed();

        if (playableCards.size() == 1) {
            return playableCards.get(0);
        } else {
            TeamId actualTeam = playerId.team();
            Node root = new Node(state, pkHand);
            for (int i = 0; i < iterations; ++i) {

                List<Node> path = promisingPath(root, hand.packed());

                Node childNode = path.get(path.size() - 1);
                Score generatedScore = childNode
                        .scoreOfRandomTurnState(hand.packed());

                long pkScore = generatedScore.packed();
                actualTeam = actualTeam.other();
                for (Node n : path) {
                    if (!n.isTerminal()) {

                        n.finishedRandomTurns += 1;
                        n.points += PackedScore.turnPoints(pkScore, actualTeam);
                        actualTeam = n.turnState.nextPlayer().team();
                    }
                }

            }

            int pkCard = PackedCardSet.get(pkHand, root.promisingNodeIndex(0));
            return Card.ofPacked(pkCard);
        }
    }

    private List<Node> promisingPath(Node root, long mctsPlayerHand) {
        List<Node> p = new ArrayList<>();
        Node n = root;
        p.add(root);
        while (!n.isTerminal() && n.isFullyExpanded()) {

            n = n.bestChild(CONSTANT_V);
            p.add(n);
        }
        if (!n.isTerminal()) {

            n = n.addChild(mctsPlayerHand);
            p.add(n);
        }
        return p;
    }

    private final static class Node {

        private TurnState turnState;
        private Node[] children;
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
        private long potentialCards(long mctsPlayerHand, PlayerId Id,
                                    TurnState turnState) {

            long playableCardsMcts = PackedTrick.playableCards(
                    turnState.packedTrick(), mctsPlayerHand);
            long playableCardsOther = PackedTrick.playableCards(
                    turnState.packedTrick(), PackedCardSet.difference(turnState.packedUnplayedCards(), mctsPlayerHand));

            return (Id == playerId) ? playableCardsMcts : playableCardsOther;

        }

        /*
         * simulates the end of a turn from a given turnState and return the
         * score obtained at the end of this turnState simulated.
         */
        private Score scoreOfRandomTurnState(long mctsPlayerHand) {

            if (isTerminal())
                return turnState.score();

            TurnState turnStateSimulated = TurnState.ofPackedComponents(turnState.packedScore(), turnState.packedUnplayedCards(), turnState.packedTrick());
            int size;
            Score turnPoints = Score.ofPacked(0);
            while (!turnStateSimulated.isTerminal()) {
                turnPoints = turnStateSimulated.score();
                long pkCardSet = potentialCards(mctsPlayerHand, turnStateSimulated.nextPlayer(), turnStateSimulated);
                size = PackedCardSet.size(pkCardSet);
                int pkCardRandom = PackedCardSet.get(pkCardSet, mctsRng.nextInt(size));

                turnStateSimulated = turnStateSimulated.withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCardRandom));
            }

            return turnPoints;
        }

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
        private Node addChild(long mctsPlayerHand) {

            int promisingCard = PackedCardSet.get(upcomingChildrenCards, 0);
            TurnState turnStateChildren = turnState
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(promisingCard));
            long cardsToDevelop;
            if (turnStateChildren.isTerminal()) {

                cardsToDevelop = PackedCardSet.EMPTY;
            } else {
                cardsToDevelop = potentialCards(mctsPlayerHand,
                        turnStateChildren.nextPlayer(), turnStateChildren);
            }

            Node childrenNode = new Node(turnStateChildren, cardsToDevelop);
            children[children.length
                    - PackedCardSet.size(upcomingChildrenCards)] = childrenNode;
            upcomingChildrenCards = PackedCardSet.remove(upcomingChildrenCards,
                    promisingCard);
            childrenNode.parent = this;

            return childrenNode;
        }

        private boolean isTerminal() {
            return children.length==0;
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