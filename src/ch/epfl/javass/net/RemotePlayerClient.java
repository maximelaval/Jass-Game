package ch.epfl.javass.net;

import ch.epfl.javass.jass.*;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.StringJoiner;

import static ch.epfl.javass.net.StringSerializer.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents the client player.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    /**
     * Constructs a remote client player with the given host name.
     *
     * @param hostName the given host name.
     */
    public RemotePlayerClient(String hostName)  {

        try {
            s = new Socket(hostName, 5108);
            r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
            w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void forward(String codedString) {

        System.out.println(codedString);
        try {

            w.write(codedString);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.CARD.name());

        String codedTrick = serializeInt(state.packedTrick());
        String codedUnplayedCards = serializeLong(state.unplayedCards().packed());
        String codedScore = serializeLong(state.packedScore());
        StringJoiner codedState = new StringJoiner(",");

        codedState.add(codedScore);
        codedState.add(codedUnplayedCards);
        codedState.add(codedTrick);

        joiner.add(codedState.toString());
        joiner.add(serializeLong(hand.packed()));
        forward(joiner.toString());

        int pkCard;
        try {
            pkCard = deserializeInt(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Card playedCard = Card.ofPacked(pkCard);
        return playedCard;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.PLRS.name());
        joiner.add(StringSerializer.serializeInt(ownId.ordinal()));
        StringJoiner names = new StringJoiner(",");
        for (String value : playerNames.values()) {
            names.add(serializeString(value));
        }
        joiner.add(names.toString());
        forward(joiner.toString());
    }

    @Override
    public void updateHand(CardSet newHand) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.HAND.name());
        joiner.add(serializeLong(newHand.packed()));
        forward(joiner.toString());
    }

    @Override
    public void setTrump(Card.Color trump) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.TRMP.name());
        joiner.add(serializeInt(trump.ordinal()));
        forward(joiner.toString());
    }

    @Override
    public void updateTrick(Trick newTrick) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.TRCK.name());
        joiner.add(serializeInt(newTrick.packed()));
        forward(joiner.toString());
    }

    @Override
    public void updateScore(Score score) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.SCOR.name());
        joiner.add(serializeLong(score.packed()));
        forward(joiner.toString());
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(JassCommand.WINR.name());
        joiner.add(serializeInt(winningTeam.ordinal()));
        forward(joiner.toString());
    }

    @Override
    public void close() {
        try {
            r.close();
            w.close();
            s.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
