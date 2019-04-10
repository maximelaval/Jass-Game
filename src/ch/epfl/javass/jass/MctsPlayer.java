package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

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

    /**
     * Constructs a MCTS player with the given seed, number of iterations of turns and identity.
     *
     * @param ownId      the given identity.
     * @param rngSeed    the given seed.
     * @param iterations the given number of iterations of turns that the player will simulate.
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {

        if (iterations < HAND_SIZE)
            throw new IllegalArgumentException();
        else {
            mctsRng = new SplittableRandom(rngSeed);

            this.iterations = iterations;
            playerId = ownId;

        }
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long pkHand = hand.packed();

        CardSet playableCards = state.trick().playableCards(hand);
        if (playableCards.size() == 1) {
            return playableCards.get(0);
        } else {
            TeamId actualTeam = playerId.team();
            Node root = new Node(state, pkHand);
            for (int i = 0; i < iterations; ++i) {

                List<Node> path = promisingPath(root, pkHand);

                Node childNode = path.get(path.size() - 1);

                Score generatedScore = childNode.scoreOfRandomTurnState(pkHand,
                        playerId, childNode.turnState);

                long pkScore = generatedScore.packed();
                actualTeam = actualTeam.other();

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

            n = n.bestChild(40);
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

        private Node(TurnState turnState, long upcomingChildrenCards) {
            this.turnState = turnState;
            this.children = new Node[PackedCardSet.size(upcomingChildrenCards)];
            this.upcomingChildrenCards = upcomingChildrenCards;
            finishedRandomTurns = 0;
            points = 0;
        }

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

        private Score scoreOfRandomTurnState(long mctsPlayerHand,
                                             PlayerId mctsPlayerId, TurnState turnState) {

            while (!turnState.isTerminal()) {
                long pkCardSet = potentialCards(mctsPlayerHand, mctsPlayerId,
                        turnState);
                int size = PackedCardSet.size(pkCardSet);
                if (size == 0) {
                    return turnState.score();
                }
                int pkCardRandom = PackedCardSet.get(pkCardSet,
                        mctsRng.nextInt(size));
                turnState = turnState.withNewCardPlayedAndTrickCollected(
                        Card.ofPacked(pkCardRandom));
            }

            return turnState.score();
        }

        private int promisingNodeIndex(int c) {
            int index = 0;
            double v = 0;
            for (int i = 0; i < children.length - PackedCardSet.size(upcomingChildrenCards); i++) {
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

        private Node addChild(long mctsPlayerHand, PlayerId mctsPlayerId) {

            int promisingCard = PackedCardSet.get(upcomingChildrenCards, 0);
            TurnState turnStateChildren = turnState
                    .withNewCardPlayedAndTrickCollected(
                            Card.ofPacked(promisingCard));
            Node childrenNode = new Node(turnStateChildren, potentialCards(
                    mctsPlayerHand, mctsPlayerId, turnStateChildren));
            children[children.length - PackedCardSet.size(upcomingChildrenCards)] = childrenNode;
            PackedCardSet.remove(upcomingChildrenCards, promisingCard);
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
            if (finishedRandomTurns > 0) {
                return ((double) points / (double) finishedRandomTurns
                        + constant
                        * Math.sqrt(2 * Math.log(parent.finishedRandomTurns)
                        / (double) finishedRandomTurns));
            } else {
                return Double.POSITIVE_INFINITY;
            }
        }

    }

}