
package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import ch.epfl.javass.jass.PackedCardSet;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public final class MctsPlayer implements Player {
    private int iterations;
    private static SplittableRandom mctsRng;
    private static PlayerId playerId ;

    MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if (iterations < HAND_SIZE) 
            throw new IllegalArgumentException();
        else {
            SplittableRandom rng = new SplittableRandom(rngSeed);
            mctsRng = new SplittableRandom(rng.nextLong());

            this.iterations = iterations;
       playerId=ownId;
        }
    }

    // private List<Card> expandTree(Node node){
    //
    // }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        
        /*
         * cardset playablacards = state.trick.playablecards(ownHand)
         * 
         * if playablecards.size == 1 -> facile, tu joues cette carte playablecards[0]
         * 
         * 
         * creer le noeud racine
         * for (i < iteratons) {
         *  
         *      chemin = promisingpath(racine, ownhand)
         *      newScore = simulate(chemin[last], ownhand)
         *      
         *      equipe courante = own.team.other
         *      for (noeuds dans le chemins) {
         *      
         *          n.iterations += 1
         *          n.pointsTotaux += turnPoints(newScore, equipe courante)
         *          equipe courante = noeud.state.nextPlayer.team
         *      }
         *      
         * }
         * 
         * 
         * ...après les iterations
         * retourner la meilleure carte parmi les enfants de la racine
         * return root.children ( retour playable.get(root.V(c = 0))
         */
        
        
        
        
        

    }
    private List<Node> promisingPath(Node root, long pkOwnHand){
        List<Node> p = new ArrayList<Node>();
        // Node n = root
        //p.add(root)
        
        /*while (not expanded && ! isLeaf){
         *  rootNode = bestChild(c)
         *  path.add(rootNode)
         *  
         *  
         *  if !leaf {
         *      rootNode.addMissingChild(own id,pkowbhand);
         *      path. add rootNode
         *  }
         *  
         *  return path
        */
        
    }

    private static class Node {

        private TurnState turnState;
        private Node[] children;
        private Node parent;
        private CardSet upcomingChildren;
        private Card nodeCard;
        private int points;
        private int finishedRandomTurns;

        private Node(TurnState turnState, long pkCardsToExpand) {
            this.turnState = turnState;
            this.children = new Node [PackedCardSet.size(pkCardsToExpand)];
            

        }
        private Node addMissingChild() {
            
        }

        private CardSet potentialCards(CardSet playerHands,PlayerId Id) {
            
            if state.isTerminal return PackedCardSet.EMPTY;
            
            if (Id==playerId) {
 
            return turnState.unplayedCards().intersection(playerHands);
        }
            else {
                return turnState.unplayedCards().difference(playerHands);
            }
        }

        private Score scoreOfRandomTurnState(CardSet hand, PlayerId Id) {

            while (!turnState.isTerminal()) {
                long pkCardSet = potentialCards(hand,Id).packed();
                int size = PackedCardSet.size(pkCardSet);
                int pkCardRandom = PackedCardSet.get(pkCardSet,
                        mctsRng.nextInt(size));
                turnState.withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCardRandom));

            }

            return turnState.score();
        }

        private int promisingNodeIndex() {
            int index = 0;
            double v = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i].V( 40) > v) {
                    v = children[i].V(40);
                    index = i;
                }
            }
            return index;
        }
        
        Node addpromisingchild (id,hand) {
         
            /*
             * choisir la premiere carte disponible
             * creer le turnstate de k enfant avec state.withnewcardandtrickcollected
             * creer l enfant avec le turnstate
             * mettre l enfant dans le tableau
             * enlever la carte jouee de l ensemble des cartes jouables
             *  optionnel: retourner l enfant
             */
        }

        is terminal vérifie return children.length=0;
        isfullxexpand return PackedCardCard.isEmpty(pkCardstoExpand);
        
        private double V( double constant) {
            assert (finishedRandomTurns >= 0);
            if (finishedRandomTurns > 0) {
                return (points / finishedRandomTurns + constant
                        * (Math.sqrt((2 * Math.log(finishedRandomTurns))
                                / parent.finishedRandomTurns)));
            } else {
                return Double.MAX_VALUE;
            }
        }

    }

}

