
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

  
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        
        
        CardSet playableCards = state.trick().playableCards(hand);
        if (playableCards.size()==1) {
            return playableCards.get(0);
        } else {
            
            Node root = new Node(state,hand.packed());
            for (int i=0;i < iterations;++i) {
                List<Node> path = promisingPath(root,hand.packed());
                
            }
            
        }
        return null;
    }
        
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
         * return root.children ( retour playable.get(root.bestChild(c = 0))
         */
        
        
        
        
        

    
    private List<Node> promisingPath(Node root, long mctsPlayerHand){
        List<Node> p = new ArrayList<Node>();
         Node n = root;
        p.add(root);
        while (!n.hasNoChild()&&n.isFullyExpanded()) {
            n = n.bestChild(40);
            p.add(n);
        } 
      n=  n.addChild(mctsPlayerHand, playerId);
       p.add(n);
       return p;
        
        
       
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
        private long upcomingChildrenCards;
        private int points;
        private int finishedRandomTurns;

        private Node(TurnState turnState, long upcomingChildrenCards) {
            this.turnState = turnState;
            this.children = new Node [PackedCardSet.size(upcomingChildrenCards)];
            this.upcomingChildrenCards =upcomingChildrenCards;
            if(parent==null) {
                finishedRandomTurns =0;
            }
            else {
                finishedRandomTurns=1;
            }
           
        }
      

     
        private long potentialCards(long mctsPlayerHand,PlayerId mctsPlayerId,TurnState turnState) {
            
            if (turnState.isTerminal()) 
                return PackedCardSet.EMPTY; 
            
            if (mctsPlayerId==playerId) {

            return PackedCardSet.intersection(turnState.packedUnplayedCards(), mctsPlayerHand);
        }
            else {
                return PackedCardSet.difference(turnState.packedUnplayedCards(), mctsPlayerHand);
               
            }
        }
        private Score scoreOfRandomTurnState(long mctsPlayerHand, PlayerId  mctsPlayerId,TurnState turnState) {

            while (!turnState.isTerminal()) {
                long pkCardSet = potentialCards(mctsPlayerHand, mctsPlayerId,turnState);
                int size = PackedCardSet.size(pkCardSet);
                int pkCardRandom = PackedCardSet.get(pkCardSet,
                        mctsRng.nextInt(size));
                turnState.withNewCardPlayedAndTrickCollected(Card.ofPacked(pkCardRandom));

            }

            return turnState.score();
        }

        private int promisingNodeIndex(int c) {
            int index = 0;
            double v = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i].V( c) > v) {
                    v = children[i].V(c);
                    index = i;
                }
            }
            return index;
        }
        
        private Node bestChild(int c) {
            return children[promisingNodeIndex(c)];
        }
        
        
        
        private TeamId nextTeamToPlay() {
            return turnState.nextPlayer().team();
       }
        
        private Node addChild (long mctsPlayerHand,PlayerId mctsPlayerId) {
       
       int promisingCard =  PackedCardSet.get(upcomingChildrenCards, 0);
        TurnState turnStateChildren =turnState.withNewCardPlayedAndTrickCollected(Card.ofPacked(promisingCard));
         Node childrenNode = new Node (turnStateChildren,potentialCards(mctsPlayerHand,mctsPlayerId,turnStateChildren));
         childrenNode.points = scoreOfRandomTurnState(mctsPlayerHand,mctsPlayerId,turnStateChildren).turnPoints(nextTeamToPlay());
         children[children.length-1] = childrenNode;
         PackedCardSet.remove(upcomingChildrenCards,promisingCard);
         childrenNode.parent =this;
         return childrenNode;
            //private void addChild (id , long pkCardSet)
         /*
             * choisir la premiere carte disponible
             * creer le turnstate de k enfant avec state.withnewcardandtrickcollected
             * creer l enfant avec le turnstate
             * mettre l enfant dans le tableau
             * enlever la carte jouee de l ensemble des cartes jouables
             *  optionnel: retourner l enfant
             */
        }

        
        private void updateFinishedRandomTurn() {
            
        }
        
        private void updatePoints() {
            
        }
        
      private boolean  hasNoChild() {
          return children.length==0 ;
      }
      
        
     private boolean isFullyExpanded() {
         return PackedCardSet.isEmpty(upcomingChildrenCards);
     }
      
        private double V( double constant) {
            assert (finishedRandomTurns >= 0);
            if (finishedRandomTurns > 0) {
                return ((double)points / (double)finishedRandomTurns + constant //                      ADD DOUBLE CAST
                        * (Math.sqrt((2 * Math.log(finishedRandomTurns))
                                / parent.finishedRandomTurns)));
            } else {
                return Double.MAX_VALUE;
            }
        }

    }

}

