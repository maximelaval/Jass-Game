package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static ch.epfl.javass.bits.Bits32.*;
import static ch.epfl.javass.jass.Jass.LAST_TRICK_ADDITIONAL_POINTS;
import static ch.epfl.javass.jass.PackedCard.color;
import static ch.epfl.javass.jass.PackedCard.isBetter;
import static ch.epfl.javass.jass.PackedCardSet.*;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet.*;

public final class PackedTrick {

    private static final int CARD_SIZE = 6;

    private PackedTrick() {

    }

    public static final int INVALID = mask(0,32);

    public static boolean isValid( int pkTrick) {
        if (
                (
                        (extract(pkTrick, 24, 4) <=8) &&
                                (extract(pkTrick, 24, 4) >=0)

                ) && (

                        ( (extract(pkTrick,18,6)!=PackedCard.INVALID) && (extract(pkTrick,12,6) !=PackedCard.INVALID )

                                &&(extract(pkTrick,6,6)!=PackedCard.INVALID) && (extract(pkTrick,0,6)!=PackedCard.INVALID)



                        ) ||


                                ( (extract(pkTrick,18,6)==PackedCard.INVALID) && (extract(pkTrick,12,6) !=PackedCard.INVALID )
                                        &&(extract(pkTrick,6,6)!=PackedCard.INVALID) && (extract(pkTrick,0,6)!=PackedCard.INVALID)



                                )    ||

                                ( (extract(pkTrick,18,6)==PackedCard.INVALID) && (extract(pkTrick,12,6) ==PackedCard.INVALID )
                                        &&(extract(pkTrick,6,6)!=PackedCard.INVALID) && (extract(pkTrick,0,6)!=PackedCard.INVALID)



                                )  ||

                                ( (extract(pkTrick,18,6)==PackedCard.INVALID) && (extract(pkTrick,12,6) ==PackedCard.INVALID )
                                        &&(extract(pkTrick,6,6)==PackedCard.INVALID) && (extract(pkTrick,0,6)!=PackedCard.INVALID)



                                ) ||

                                ( (extract(pkTrick,18,6)==PackedCard.INVALID) && (extract(pkTrick,12,6) ==PackedCard.INVALID )
                                        &&(extract(pkTrick,6,6)==PackedCard.INVALID) && (extract(pkTrick,0,6)==PackedCard.INVALID)



                                )

                )
        )

        {
            return true;
        } else {
            return false;
        }
    }

    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return pack(PackedCard.INVALID ,6,PackedCard.INVALID ,6,PackedCard.INVALID ,6,PackedCard.INVALID,6 ,0, 4, firstPlayer.ordinal(), 2, trump.ordinal(), 2);
    }

    public static int nextEmpty(int pkTrick) {
        assert isFull(pkTrick);
        if (isLast(pkTrick)) {
            return INVALID;
        } else {
            return  pack(
                    PackedCard.INVALID, 6,
                    PackedCard.INVALID, 6,
                    PackedCard.INVALID, 6,
                    PackedCard.INVALID, 6,
                    index(pkTrick)+1, 4,
                    winningPlayer(pkTrick).ordinal(), 2,
                    trump(pkTrick).ordinal(), 2);

        }
    }

    public static boolean isLast(int pkTrick) {
        return index(pkTrick) == 8;
    }

    public static boolean isEmpty(int pkTrick) {
        return size(pkTrick) == 0;
    }

    public static boolean isFull(int pkTrick) {
        return size(pkTrick) == 4;
    }

    public static int size(int pkTrick) {

        for (int i = 4; i > 0; --i) {
            if (PackedCard.isValid(card(pkTrick, i -1))) {
                return i;
            }
        }
        return 0;
    }

    public static Card.Color trump(int pkTrick) {
        return Color.values()[extract(pkTrick, 30, 2)];
    }

    public static PlayerId player(int pkTrick, int index) {
        int firstPlayer = extract(pkTrick, 28, 2);
        int player = (firstPlayer + index) % 4;
        return PlayerId.values()[player];
    }

    public static int index(int pkTrick) {
        return extract(pkTrick, 24, 4);
    }

    public static int card(int pkTrick, int index) {
        return extract(pkTrick, 6 * index, 6);
    }

    public static int withAddedCard(int pkTrick, int pkCard) {
        assert(!isFull(pkTrick));
        int var = 6 * size(pkTrick);
        return (~mask(var,6)& pkTrick)| (pkCard<<var);

    }
    public static Card.Color baseColor(int pkTrick) {
        assert(!isEmpty(pkTrick));
        return color(card(pkTrick, 0));
    }

    public static long playableCards(int pkTrick, long pkHand) {

        assert isValid(pkTrick);

        assert ! isFull(pkTrick);

        if (isEmpty(pkTrick))return pkHand;

        Color trump = trump(pkTrick);
        long tr = PackedCardSet.subsetOfColor(pkHand, trump);

        long best;
        if(baseColor(pkTrick) == trump(pkTrick)) {
            best = tr;
        }else {
            best = tr;
            for (int i = 1; i < size(pkTrick); ++i) {
                int pkCard = card(pkTrick, i);
                if (PackedCard.color(pkCard) == trump(pkTrick))
                    best = PackedCardSet.intersection(best, PackedCardSet.trumpAbove(pkCard));
            }
        }

        long base = PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
        long j = PackedCardSet.singleton(PackedCard.pack(trump, Rank.JACK));
        long rest = PackedCardSet.difference(pkHand, PackedCardSet.union(base, tr));

        if (base == j) return pkHand;
        else if (! PackedCardSet.isEmpty(base))return PackedCardSet.union(base, best);
        else if (! PackedCardSet.isEmpty(rest))return PackedCardSet.union(rest, best);
        else if (! PackedCardSet.isEmpty(best))return best;
        else return pkHand;
    }



    public static int points(int pkTrick) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            if(card(pkTrick,i)!=PackedCard.INVALID) {
                result += PackedCard.points(trump(pkTrick), card(pkTrick, i));
            }
        }
        if (isLast(pkTrick)) {
            result += LAST_TRICK_ADDITIONAL_POINTS;
        }
        return result;
    }

    public static PlayerId winningPlayer(int pkTrick) {
        int i = 0;
        int j = card(pkTrick,0);
        if(card(pkTrick,1) != PackedCard.INVALID && PackedCard.isBetter(Color.ALL.get(extract(pkTrick,30,2)), card( pkTrick,1), j)) {
            i = 1;
            j = card( pkTrick,1);
        }
        if( card( pkTrick,2) != PackedCard.INVALID && PackedCard.isBetter(Color.ALL.get(extract(pkTrick,30,2)),  card( pkTrick,2), j)) {
            i = 2;
            j =  card( pkTrick,2);
        }
        if( card( pkTrick,3) != PackedCard.INVALID && PackedCard.isBetter(Color.ALL.get(extract(pkTrick,30,2)),  card( pkTrick,3), j)) {
            i = 3;
            j = card( pkTrick,3);
        }
        return player(pkTrick,i);
    }


    public static String toString(int pkTrick) {
        assert(isValid(pkTrick));

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); ++i) {
            j.add(Card.ofPacked(card(pkTrick, i)).toString());
        }
        return j.toString();
    }

}
