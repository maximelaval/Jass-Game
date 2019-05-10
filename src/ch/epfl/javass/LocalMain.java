package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.*;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

public class LocalMain extends Application {

    private final static String DELIMITER = ":";
    private final static String[] DEFAULT_NAMES = new String[]{"Aline", "Bastien", "Colette", "David"};
    private final static String DEFAULT_HOST_NAME = "localHost";
    private final static int DEFAULT_ITERATIONS = 10_000;
    private final static double MIN_TIME_PACED_PLAYER = 1.;
    private final static long WAITING_TIME_END_TRICK = 1000;

    private Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
    private Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Random randomGenerator;

        List<String> argsList = this.getParameters().getRaw();
        System.out.println(argsList);


        if (!(argsList.size() >= 4 && argsList.size() <= 5)) {
            System.err.println(helpText());
            System.exit(1);
        }

        if (argsList.size() == 5) {
            randomGenerator = new Random(Long.parseLong(argsList.get(4)));
        } else {
            randomGenerator = new Random();
        }
        Long jassGameRngSeed = randomGenerator.nextLong();


        try {
            createPlayers(argsList, randomGenerator);
        } catch (Error e) {
            System.err.println("Erreur : spécification de joueur invalide : " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Erreur : le nombre d'itération n'est pas un nombre valide.");
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(jassGameRngSeed, ps, ns);
            while (! g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try { Thread.sleep(WAITING_TIME_END_TRICK); } catch (Exception e) {}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();

    }

    private String helpText() {
        return "Utilisation: java ch.epfl.javass.LocalMain <j1> <j2> <j3> <j4> [<graine>]\n" +
                "où :\n" +
                "<jn> spécifie le joueur n, ainsi:\n" +
                "  h:<nom>  un joueur humain nommé <nom>\n" +
                "  s:<nom> un joueur simulé nommé <nom>\n" +
                "  r:<nom>:<host_name> un joueur distant nommé <nom>\n" +
                "<graine> spécifie la graine aléatoire du jeu";
    }

    private void createPlayers(List<String> argsList, Random randomGenerator) {
        String[] playerTypes = new String[4];
        String[] playerNames = new String[4];
        String[] hostNames = new String[4];
        Integer[] iterations = new Integer[4];


        for (int i = 0; i < PlayerId.COUNT; i++) {
            String arg = argsList.get(i);
            String[] playerParameters = StringSerializer.split(DELIMITER, arg);

            String playerType = playerParameters[0];
            if (!(playerType.equals("s") || playerType.equals("h") || playerType.equals("r"))) {
                throw new Error(playerType);
            }
            playerTypes[i] = playerType;

            if (playerParameters.length >= 2) {
                if (playerParameters[1].equals("")) {
                    playerNames[i] = DEFAULT_NAMES[i];
                } else {
                    playerNames[i] = (playerParameters[1]);
                }
            } else {
                playerNames[i] = (DEFAULT_NAMES[i]);
            }

            if (playerType.equals("r")) {
                if (playerParameters.length >= 3) {
                    hostNames[i] = playerParameters[2];
                } else {
                    hostNames[i] = DEFAULT_HOST_NAME;
                }
            }

            if (playerType.equals("s")) {
                if (playerParameters.length >= 3) {
                    iterations[i] = Integer.parseInt(playerParameters[2]);
                } else {
                    iterations[i] = DEFAULT_ITERATIONS;
                }
            }


        }
        System.out.println(Arrays.toString(playerTypes));
        System.out.println(Arrays.toString(playerNames));
        System.out.println(Arrays.toString(hostNames));
        System.out.println(Arrays.toString(iterations));

        for (int i = 0; i < PlayerId.COUNT; i++) {
            int j = i;
            switch (playerTypes[i]) {
                case "h":
                    ps.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());
                    break;
                case "s":
                    ps.put(PlayerId.ALL.get(i), new PacedPlayer(
                            new MctsPlayer(PlayerId.ALL.get(i), randomGenerator.nextLong(), iterations[i]),
                            MIN_TIME_PACED_PLAYER));
                    break;
                case "r":
                    ps.put(PlayerId.ALL.get(i), new RemotePlayerClient(hostNames[i]));
                    break;
                default:
                    throw new Error();
            }
            ns.put(PlayerId.ALL.get(i), playerNames[i]);
//            PlayerId.ALL.forEach(k -> ns.put(k, playerNames));
        }



    }
}
