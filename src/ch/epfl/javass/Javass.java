package ch.epfl.javass;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PackedTrick;
import ch.epfl.javass.jass.Trick;

public class Javass {
    public static void main(String[] args) {
        int i = 0b10_00_0000_000000_000000_110010_110001;
        
        long h = 0b0000000_000000000_0000000_110000110_1000100_000001000_0000011_000000001L;
//        System.out.println(PackedTrick.trump(i));
//        System.out.println(Integer.toBinaryString(-2113929742));
//        System.out.println(Long.toBinaryString(75435362496807168L));
        System.out.println(Card.ofPacked(PackedTrick.card(i, 2)));

    }

}
