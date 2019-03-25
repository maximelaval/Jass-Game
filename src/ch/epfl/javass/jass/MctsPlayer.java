package ch.epfl.javass.jass;

import static ch.epfl.javass.jass.Jass.HAND_SIZE;

public final class MctsPlayer implements Player  {

    MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if (iterations < HAND_SIZE)
            throw new IllegalArgumentException();
    }

}
