
package ch.epfl.javass.jass;



import java.util.List;
import java.util.SplittableRandom;
import ch.epfl.javass.jass.PackedCardSet;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

  


    public final class MctsPlayer implements Player  {
        private int iterations;
       private static  SplittableRandom   mctsRng;
       
        MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
            if (iterations < HAND_SIZE)
                throw new IllegalArgumentException();
            else {
                SplittableRandom rng = new SplittableRandom(rngSeed);
                mctsRng = new SplittableRandom(rng.nextLong());

             this.iterations = iterations ;
            }
        }
        
     
 
//private List<Card> expandTree(Node node){
//   
//}
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        int i = 0;
        Node rootNode = new Node(state);
        while(i<iterations) {
            
   
        }
        
        
    }
    private static  class Node {

        private TurnState turnState;
        private Node[] children;
        private Node parent;
        private CardSet upcomingChildren;
        private int points;
        private int finishedRandomTurns;
      

     private  Node(TurnState turnState) {
this.turnState = turnState;
this.points = scoreOfRandomTurnState(turnState.);
        }
     
        
     private CardSet potentialCards(CardSet playerHands) {
      
 return turnState.unplayedCards().union(playerHands);
     }
     
     private Score scoreOfRandomTurnState( CardSet hand ) {
         
         while(!turnState.isTerminal()) {
        long  pkCardSet = potentialCards(hand).packed();
        int size = PackedCardSet.size(pkCardSet);
       int pkCardRandom = PackedCardSet.get(pkCardSet,mctsRng.nextInt(size) );
       turnState.withNewCardPlayed(Card.ofPacked(pkCardRandom));
          
       }
          
             return turnState.score();
         }
     
        private  int  promisingNodeIndex(){
            int index = 0;
            double v = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i].V(points, 40) > v) {
                    v = children[i].V(points, 40);
                    index = i;
                }
         }
            return index;
        }

        
       
        private double V (int points, double constant){
            assert(finishedRandomTurns >= 0);
            if (finishedRandomTurns > 0) {
                return (points / finishedRandomTurns + constant * (Math.sqrt((2 * Math.log(finishedRandomTurns)) / parent.finishedRandomTurns)));
            } else {
                return Double.MAX_VALUE;
            }
        }

    }



}
