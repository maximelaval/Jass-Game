package ch.epfl.javass.net;

import ch.epfl.javass.jass.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static ch.epfl.javass.net.StringSerializer.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerServer {

    private final Player localPlayer;

    public RemotePlayerServer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    public void run() {

        try (ServerSocket s0 = new ServerSocket(5108);
             Socket s = s0.accept();
             BufferedReader r =
                     new BufferedReader((
                             new InputStreamReader(s.getInputStream(), US_ASCII)));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {

            String receivedString = r.readLine();
            String[] stringArray = split(receivedString, " ");
            String condensedMethodName = stringArray[0];

            String codedHand = "";
            String parametersString = "";
            String[] parametersArray = null;

            switch (condensedMethodName) {
                case "PLRS":
                    PlayerId ownId = PlayerId.values()[Integer.parseUnsignedInt(stringArray[1])];

                    parametersString = stringArray[2];
                    parametersArray = split(parametersString, ",");
                    // ArrayList<String> decodedPlayerNames = new ArrayList<>(PlayerId.COUNT);
                    Map<PlayerId, String> playersMap = new HashMap<>();

                    for (int i = 0; i < PlayerId.COUNT; ++i) {
                        playersMap.put(PlayerId.values()[i], deserializeString(parametersArray[i]));

                    }

                    localPlayer.setPlayers(ownId, playersMap);
                    break;

                case "TRMP":
                    int colorOrdinal = Integer.parseUnsignedInt(stringArray[1]);
                    localPlayer.setTrump(Card.Color.values()[colorOrdinal]);
                    break;

                case "HAND":
                    codedHand = stringArray[1];
                    long decodedHand = deserializeLong(codedHand);
                    localPlayer.updateHand(CardSet.ofPacked(decodedHand));
                    break;

                case "TRCK":

                    localPlayer.updateTrick();
                case "CARD":
                    parametersString = stringArray[1];
                    parametersArray = split(parametersString, ",");

                    long pkScore = deserializeLong(parametersArray[0]);
                    long pkUplayedCards = deserializeLong(parametersArray[1]);
                    int pkTrick = deserializeInt(parametersArray[2]);

                    TurnState turnState = TurnState.ofPackedComponents(pkScore, pkUplayedCards, pkTrick);
                    codedHand = stringArray[2];
                    localPlayer.cardToPlay(turnState, CardSet.ofPacked(deserializeLong(codedHand)));
                    break;

                case "SCOR":
                    String codedScore = stringArray[1];
                    long decodedScore = deserializeLong(codedScore);
                    localPlayer.updateScore(Score.ofPacked(decodedScore));
                    break;

                case "WINR":


                    localPlayer.setWinningTeam();
                default:
                    throw new Error("Invalid method name for a player");
            }

            int i = Integer.parseInt(r.readLine());
            int i1 = i + 1;
            w.write(String.valueOf(i1));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
