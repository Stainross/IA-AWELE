package awele.bot.robin;

import awele.bot.CompetitorBot;
import awele.bot.demo.random.RandomBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;


import java.util.Arrays;


public class MinMaxBetter extends CompetitorBot {

    private int depth[];  //le tableau de profondeur en fonction du nombre de graine, eventuellement suppl�mente par une heuristique

    private int maDepth;


    public MinMaxBetter() throws InvalidBotException {
        maDepth = 7;
        depth = new int[49];
        for (int i = 0; i < 49; i++) {
            depth[i] = maDepth - (i / 10);
        }
        this.setBotName("IAWELEBetter");
        this.addAuthor("R. LAGLER, T. FELKER");
    }

    public MinMaxBetter(int depthGiven) throws InvalidBotException {
        maDepth = depthGiven;
        depth = new int[49];
        for (int i = 0; i < 49; i++) {
            depth[i] = maDepth - (i / 10);
        }
        this.setBotName("IAWELEBetter - avec heuristique et profondeur 15+2");
        this.addAuthor("R. LAGLER, T. FELKER");
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub

    }

    /**
     * la fonction de d�cision, calcule la profondeur a utiliser pour calculer l'efficacit� des coups comme suit :
     * Pour chaque coup valide, prend la valeur du tableau de profondeur calcul� pendant learn et lui ajoute 1 tous les 2 coups invalides
     *
     * @param board Le plateau de jeu courant
     * @return un tableau avec l'efficacit� de chaque coup
     */
    @Override
    public double[] getDecision(Board board) {

        double[] decision = new double[6];
        int curseed = board.getNbSeeds();
        int curp = board.getCurrentPlayer();
        boolean[] validMoves = board.validMoves(curp);
        double[] decisionBis = new double[6];
        int unvalid = 0;
        if (board.getNbSeeds() == 48) {
            if (Arrays.equals(board.getPlayerHoles(), new int[]{4, 4, 4, 4, 4, 4})) {
                return new double[]{0, 0, 0, 0, 0, 10};
            }
        }
        for (int j = 0; j < 6; j++) {
            if (!validMoves[j])
                unvalid += 1;
        }
        int maprof = depth[curseed] + unvalid / 2;

        for (int i = 0; i < 6; i++) {
            if (validMoves[i]) {
                decisionBis[i] = i + 1;
                try {
                    decision[i] = miniMax(-100, 100, maprof, board.playMoveSimulationBoard(curp, decisionBis), false);

                } catch (InvalidBotException e) {
                }
            }
        }


        return decision;
    }

    /**
     * @param board le plateau de jeu courant
     * @param curp  le joueur courant
     * @return si le plateau actuel repr�sente une partie finie ou non
     */
    public boolean isFinalLeaf(Board board, int curp) {
        return
                board.getScore(1 - curp) > 24 ||
                        board.getNbSeeds() < 7 ||
                        isOnlyInvalidMoves(board.validMoves(curp));
    }

    /**
     * @param validMoves un tableau disant si les 6 coups sont valides ou non
     * @return true si aucun coup n'est valide
     */
    private boolean isOnlyInvalidMoves(boolean[] validMoves) {
        for (int i = 0; i < 6; i++)
            if (validMoves[i]) return false;
        return true;
    }


    public static final int TALLY_WEIGHT = 30;

    /**
     * Weight of houses that contain more than 12 seeds
     */
    public static final int ATTACK_WEIGHT = 28;

    /**
     * Weight of houses that contain 1 or 2 seeds
     */
    public static final int DEFENSE_WEIGHT = -36;

    /**
     * Weight of houses that do not contain any seeds
     */
    public static final int MOBILITY_WEIGHT = -54;


    /**
     * @param board le plateau de jeu courant
     * @return une partie de la fonction evaluation en fonction du nombre de krous et trous dangereux du joueur
     */
    public double getBoardState(Board board) {

        int calculated_score = board.getScore(board.getCurrentPlayer()) - board.getScore(1 - board.getCurrentPlayer());

        int score = TALLY_WEIGHT * (calculated_score); //Heuristique a nous

        int[] oppHoles = board.getOpponentHoles();
        int[] playerHoles = board.getPlayerHoles();
        for (int i = 0; i <= 5; i++) {
            final int seedOpp = oppHoles[i];
            final int seedPlayer = playerHoles[i];
            if (seedPlayer > (12 - i)) {
                score += ATTACK_WEIGHT;
            } else if (seedPlayer == 0) {
                score += MOBILITY_WEIGHT;
            } else if (seedPlayer < 3) {
                score += DEFENSE_WEIGHT;
            }


            if (seedOpp > (12 - i)) {
                score -= ATTACK_WEIGHT;
            } else if (seedOpp == 0) {
                score -= MOBILITY_WEIGHT;
            } else if (seedOpp < 3) {
                score -= DEFENSE_WEIGHT;
            }
        }


        return score;
    }

    /**
     * @param alpha La valeur alpha de l'algorithme minimax
     * @param beta  La valeur beta de l'algorithme minimax
     * @param depth La profondeur qu'il reste a parcourir
     * @param board Le plateau de jeu courant
     * @param isMax S'il s'agit du joueur devant maximiser ou non
     * @return La valeur du coup �tudi� s'il s'agit d'une feuille ou si la profondeur maximale est atteinte, joue les coups suivant sinon
     * @throws InvalidBotException Recup�re les erreurs si le bot plante pour ne pas faire s'arr�ter le programme
     */
    public double miniMax(double alpha, double beta, int depth, Board board, boolean isMax) throws InvalidBotException {

        int curp = board.getCurrentPlayer();

        if (depth == 0 && board.getNbSeeds() < 20) {
            int unvalid = 0;
            boolean[] validMoves = board.validMoves(curp);
            for (int j = 0; j < 6; j++) {
                if (!validMoves[j])
                    unvalid += 1;
            }
            if (unvalid > 3) {
                depth = 1;
            }
        }

        if (depth == 0 || isFinalLeaf(board, curp))
            return getBoardState(board) * (isMax ? 1 : -1);

        double v = 0;
        boolean[] validMoves = board.validMoves(curp);
        double[] decisionBis = new double[6];

        if (isMax) {
            v = -100;
            for (int i = 0; i < 6; i++) {
                if (validMoves[i]) {
                    decisionBis[i] = i + 1;
                    double minmax = miniMax(alpha, beta, depth - 1, board.playMoveSimulationBoard(curp, decisionBis), false);
                    v = v > minmax ? v : minmax;
                    if (v >= beta) return v;
                    alpha = v > alpha ? v : alpha;
                }
            }
        } else {
            v = 100;
            for (int i = 0; i < 6; i++) {
                if (validMoves[i]) {
                    decisionBis[i] = i + 1;
                    double minmax = miniMax(alpha, beta, depth - 1, board.playMoveSimulationBoard(curp, decisionBis), true);
                    v = v < minmax ? v : minmax;
                    if (v <= alpha) return v;
                    beta = v < beta ? v : beta;
                }
            }
        }

        return v;
    }


    public int[] getBoardHash(Board b){
        int[] hash = new int[14];
        for(int i = 0; i<6; i++){
            hash[i] = b.getPlayerHoles()[i];
            hash[i+7] = b.getOpponentHoles()[i];
        }
        hash[6] = b.getScore(0);
        hash[13] = b.getScore(1);
        return hash;
    }
    /**
     *
     * la fonction d'apprentissage permettant d'initialiser les variables et �galement de les ajuster pendant les tests
     */
    @Override
    public void learn() {

        int goodDepth = this.maDepth;
        RandomBot random = null;
        try {
            random = new RandomBot();
            random.learn();
        } catch (InvalidBotException e) {
            throw new RuntimeException(e);
        }
        long randomRunningTime = 0;
        int nbMoves = 0;

        for (int k = 0; k < 100; k++) {
            Awele awele = new Awele(random, random);
            try {
                awele.play();
            } catch (InvalidBotException e) {
                throw new RuntimeException(e);
            }
            nbMoves += awele.getNbMoves();
            randomRunningTime += awele.getRunningTime();
        }
        long randomAverageDecisionTime = randomRunningTime / nbMoves;

        try {
            for (int i = 1; i < 100; i++) {
                MinMaxBetter b;
                b = new MinMaxBetter(this.maDepth + i);
                System.out.println("Test de la profondeur " + (this.maDepth + i));
                Awele awele = new Awele(b, random);
                awele.play();
                long decisionTime = (long) ((2 * awele.getRunningTime()) / awele.getNbMoves()) - randomAverageDecisionTime;
                if (decisionTime > 0.80 * 200) {
                    goodDepth = this.maDepth + i - 1;
                    System.out.println("C'est bon : " + decisionTime + " ; " + goodDepth);

                    break;
                } else {
                    System.out.println("On augmente ! " + decisionTime);
                }
            }
        } catch (InvalidBotException e) {
            throw new RuntimeException(e);
        }
        this.maDepth = goodDepth;
        System.out.println("Profondeur finale : " + this.maDepth);
        /* initialisation des variables */

        depth = new int[49];
        /*valeurs par d�faut du tableau de profondeur. Plus la partie augmente plus la profondeur est grande */
        for (int i = 0; i < 49; i++) {
            depth[i] = maDepth - (i / 10);
        }

    }


}
