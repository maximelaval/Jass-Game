package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.*;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LocalMain extends Application {

    private final static String DELIMITER = ":";
    private final static String[] DEFAULT_NAMES = new String[]{"Aline", "Bastien", "Colette", "David"};
    private final static String DEFAULT_HOST_NAME = "localHost";
    private final static int DEFAULT_ITERATIONS = 10_000;
    // Unit : second
    private final static double MIN_TIME_PACED_PLAYER = 0.5;
    //Unit : millisecond
    private final static long WAITING_TIME_END_TRICK = 500;
    private final static int MINIMUM_ITERATIONS = 10;

    private Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
    private Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Random randomGenerator = new Random();

        List<String> argsList = this.getParameters().getRaw();
        System.out.println(argsList);


        if ((argsList.size() < 4 || argsList.size() > 5)) {
            System.err.println(helpText());
            System.exit(1);
        }

        if (argsList.size() == 5) {
            try {
                long seed = Long.parseLong(argsList.get(4));
                randomGenerator = new Random(seed);
                if (seed <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("Erreur : la graine spécifiée n'est pas un entier <long> valide");
                System.exit(1);
            }
        }
        long jassGameRngSeed = randomGenerator.nextLong();

        try {
            createPlayers(argsList, randomGenerator);
        } catch (Error | NumberFormatException e) {
            System.err.println("Erreur : " + e.getMessage());
            System.exit(1);
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(jassGameRngSeed, ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(WAITING_TIME_END_TRICK);
                } catch (Exception e) {
                }
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

        for (int i = 0; i < PlayerId.COUNT; i++) {
            String arg = argsList.get(i);
            String[] playerParameters = StringSerializer.split(DELIMITER, arg);

            String playerType = playerParameters[0];
            String playerName;
            String hostName = "";
            int iterations = 0;

            if (!(playerType.equals("s") || playerType.equals("h") || playerType.equals("r"))) {
                throw new Error("Spécification de joueur invalide : " + playerType);
            }

            if (playerParameters.length >= 2) {
                if (playerParameters[1].equals("")) {
                    playerName = DEFAULT_NAMES[i];
                } else {
                    playerName = (playerParameters[1]);
                }
            } else {
                playerName = (DEFAULT_NAMES[i]);
            }

            if (playerType.equals("h")) {
                if (playerParameters.length >= 3) {
                    throw new Error("Le nombre de paramètres passés pour un joueur humain est trop grand.");
                }
            }

            if (playerType.equals("r")) {
                if (playerParameters.length > 3)
                    throw new Error("Le nombre d'arguments passés pour un joueur distant est trop grand.");
                if (playerParameters.length == 3) {
                    hostName = playerParameters[2];
                } else {
                    hostName = DEFAULT_HOST_NAME;
                }
            }

            if (playerType.equals("s")) {
                if (playerParameters.length > 3)
                    throw new Error("Le nombre d'arguments passés pour un joueur simulé est trop grand.");
                if (playerParameters.length == 3) {
                    if (Integer.parseInt(playerParameters[2]) < MINIMUM_ITERATIONS)
                        throw new NumberFormatException("Erreur : le nombre d'itération n'est pas un nombre valide.");
                    iterations = Integer.parseInt(playerParameters[2]);
                } else {
                    iterations = DEFAULT_ITERATIONS;
                }
            }

            int j = i;
            switch (playerType) {
                case "h":
                    ps.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());
                    break;
                case "s":
                    ps.put(PlayerId.ALL.get(i), new PacedPlayer(
                            new MctsPlayer(PlayerId.ALL.get(i), randomGenerator.nextLong(), iterations),
                            MIN_TIME_PACED_PLAYER));
                    break;
                case "r":
                    ps.put(PlayerId.ALL.get(i), new RemotePlayerClient(hostName));
                    break;
                default:
                    throw new Error();
            }
            ns.put(PlayerId.ALL.get(i), playerName);

            System.out.println(playerType + " " + playerName + " " + hostName + " " + iterations);

        }
    }
}
