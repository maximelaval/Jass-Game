package ch.epfl.javass.net;

import ch.epfl.javass.jass.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static ch.epfl.javass.net.StringSerializer.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents a server player.
 *
 * @author Lucas Meier (283726)
 * @author Maxime Laval (287323)
 */
public final class RemotePlayerServer {

    private final Player localPlayer;

    /**
     * Constructs a remote server player attached to the given local player.
     *
     * @param localPlayer the given local player.
     */
    public RemotePlayerServer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    /**
     * run the server indefinitely until the game isi ended.
     */
    public void run() {

//        while (true) {
            try (ServerSocket s0 = new ServerSocket(5108);
                 Socket s = s0.accept();
                 BufferedReader r =  new BufferedReader((new InputStreamReader(s.getInputStream(), US_ASCII)));
                 BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {

                 while (true) {
                     String receivedString = r.readLine();
                     // gerer null lorsque le client ferme
                     System.out.println("received string : " + receivedString);
                     String[] stringArray = split(" ", receivedString);
                     String condensedMethodName = stringArray[0];

                     String codedHand;
                     String parametersString;
                     String[] parametersArray;

                     switch (condensedMethodName) {
                         case "PLRS":
                             PlayerId ownId = PlayerId.values()[Integer.parseUnsignedInt(stringArray[1])];

                             parametersString = stringArray[2];
                             parametersArray = split(",", parametersString);
                             // ArrayList<String> decodedPlayerNames = new ArrayList<>(PlayerId.COUNT);
                             Map<PlayerId, String> playersMap = new HashMap<>();

                             for (int i = 0; i < PlayerId.COUNT; ++i) {
                                 playersMap.put(PlayerId.values()[i], deserializeString(parametersArray[i]));
                             }

                             localPlayer.setPlayers(ownId, playersMap);
                             break;

                         case "TRMP":
                             int colorOrdinal = deserializeInt(stringArray[1]);
                             localPlayer.setTrump(Card.Color.values()[colorOrdinal]);
                             break;

                         case "HAND":
                             codedHand = stringArray[1];
                             long decodedHand = deserializeLong(codedHand);
                             localPlayer.updateHand(CardSet.ofPacked(decodedHand));
                             break;

                         case "TRCK":
                             int packedTrick = deserializeInt(stringArray[1]);
                             localPlayer.updateTrick(Trick.ofPacked(packedTrick));
                             break;
                         case "CARD":
                             parametersString = stringArray[1];
                             parametersArray = split(",", parametersString);

                             long pkScore = deserializeLong(parametersArray[0]);
                             long pkUplayedCards = deserializeLong(parametersArray[1]);
                             int pkTrick = deserializeInt(parametersArray[2]);

                             TurnState turnState = TurnState.ofPackedComponents(pkScore, pkUplayedCards, pkTrick);
                             codedHand = stringArray[2];
                             Card card = localPlayer.cardToPlay(turnState, CardSet.ofPacked(deserializeLong(codedHand)));
                             String codedPkCard = serializeInt(card.packed());
                             w.write(codedPkCard);
                             w.write('\n');
                             w.flush();
                             break;

                         case "SCOR":
                             String codedScore = stringArray[1];
                             long decodedScore = deserializeLong(codedScore);
                             localPlayer.updateScore(Score.ofPacked(decodedScore));
                             break;

                         case "WINR":
                             int winningTeamOrdinal = deserializeInt(stringArray[1]);
                             localPlayer.setWinningTeam(TeamId.values()[winningTeamOrdinal]);
                             break;

                         default:
                             throw new Error("Invalid method name for a player");
                     }
                 }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }


    }
}
