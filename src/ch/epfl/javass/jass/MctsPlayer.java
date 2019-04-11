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
    private int CONSTANT_V = 40;

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
        long pkHand = hand.packed();

        CardSet playableCards = state.trick().playableCards(hand);
        if (playableCards.size() == 1) {
            return playableCards.get(0);
        } else {

            Node root = new Node(state, pkHand);
            for (int i = 0; i < iterations; ++i) {
                TeamId actualTeam = playerId.team();
                List<Node> path = promisingPath(root, pkHand);

                Node childNode = path.get(path.size() - 1);

                Score generatedScore = childNode.scoreOfRandomTurnState(pkHand,
                        playerId, childNode.turnState);

                long pkScore = generatedScore.packed();

                for (Node n : path) {
                    n.finishedRandomTurns += 1;
                    n.points += PackedScore.turnPoints(pkScore, actualTeam);

                    actualTeam = n.turnState.nextPlayer().team();
                }

            }
            int pkCard = PackedCardSet.get(
                    root.potentialCards(pkHand, playerId, state),
                    root.promisingNodeIndex(0));
            return Card.ofPacked(pkCard);
        }
    }

    private List<Node> promisingPath(Node root, long mctsPlayerHand) {
        List<Node> p = new ArrayList<>();
        Node n = root;
        p.add(root);
        while (!n.hasNoChild() && n.isFullyExpanded()) {

            n = n.bestChild(CONSTANT_V);
            p.add(n);
        }
        if (!n.hasNoChild()) {
            n = n.addChild(mctsPlayerHand, playerId);
            p.add(n);
        }
        return p;
    }

    private static class Node {

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
        private long potentialCards(long mctsPlayerHand, PlayerId mctsPlayerId,
                                    TurnState turnState) {

            if (turnState.isTerminal())
                return PackedCardSet.EMPTY;

            if (mctsPlayerId == playerId) {

                return PackedCardSet.intersection(
                        turnState.packedUnplayedCards(), mctsPlayerHand);
            } else {
                return PackedCardSet.difference(turnState.packedUnplayedCards(),
                        mctsPlayerHand);
            }
        }

        /*
         * simulates the end of a turn from a given turnState and return the
         * score obtained at the end of this turnState simulated.
         */
        private Score scoreOfRandomTurnState(long mctsPlayerHand,
                                             PlayerId mctsPlayerId, TurnState turnState) {
            long pkCardSet = potentialCards(mctsPlayerHand, mctsPlayerId,
                    turnState);
            int size = PackedCardSet.size(pkCardSet);
            while (size != 0) {

                int pkCardRandom = PackedCardSet.get(pkCardSet,
                        mctsRng.nextInt(size));
                turnState = turnState.withNewCardPlayedAndTrickCollected(
                        Card.ofPacked(pkCardRandom));
                pkCardSet = potentialCards(mctsPlayerHand, mctsPlayerId,
                        turnState);
                size = PackedCardSet.size(pkCardSet);
            }

            return turnState.score();
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
        private Node addChild(long mctsPlayerHand, PlayerId mctsPlayerId) {

            int promisingCard = PackedCardSet.get(upcomingChildrenCards, 0);
            TurnState turnStateChildren = turnState
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(promisingCard));
            Node childrenNode = new Node(turnStateChildren, potentialCards(
                    mctsPlayerHand, mctsPlayerId, turnStateChildren));
            children[children.length
                    - PackedCardSet.size(upcomingChildrenCards)] = childrenNode;
            upcomingChildrenCards = PackedCardSet.remove(upcomingChildrenCards,
                    promisingCard);
            childrenNode.parent = this;
            return childrenNode;
        }

        private boolean hasNoChild() {
            return children.length == 0;
        }

        private boolean isFullyExpanded() {
            return PackedCardSet.isEmpty(upcomingChildrenCards);
        }

        private double V(double constant) {
            assert (finishedRandomTurns >= 0);
            return finishedRandomTurns > 0
                    ? ((double) points / (double) finishedRandomTurns + constant
                    * Math.sqrt(2 * Math.log(parent.finishedRandomTurns)
                    / (double) finishedRandomTurns))
                    : Double.POSITIVE_INFINITY;

        }

    }

}