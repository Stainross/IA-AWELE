package awele.bot.Lucas;

import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNodeAwelicopter
{
    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;
    static int compteur = 0;

    public static int nbGrainesTotale = 48;
    /**
     * Constructeur...
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    public MinMaxNodeAwelicopter (Board board, int depth, double alpha, double beta) {
            /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
            this.decision = new double[Board.NB_HOLES];
            /* Initialisation de l'évaluation courante */
            this.evaluation = this.worst();

            if(Awelicopter.goodDepth + 4 - (board.getNbSeeds() / 10)>maxDepth&&Awelicopter.goodDepth!=0){
                maxDepth = Awelicopter.goodDepth + 4 - (board.getNbSeeds() / 10);
            }

            /* On parcourt toutes les coups possibles */
            for (int i = 0; i < Board.NB_HOLES; i++) {
                /* Si le coup est jouable */
                if (board.getPlayerHoles()[i] != 0) {
                    /* Sélection du coup à jouer */
                    double[] decision = new double[Board.NB_HOLES];
                    decision[i] = 1;
                    /* On copie la grille de jeu et on joue le coup sur la copie */
                    Board copy = (Board) board.clone();
                    try {
                        int score = copy.playMoveSimulationScore(copy.getCurrentPlayer(), decision);
                        copy = copy.playMoveSimulationBoard(copy.getCurrentPlayer(), decision);
                    /* Si la nouvelle situation de jeu est un coup qui met fin à la partie,
                       on évalue la situation actuelle */
                        if ((score < 0) ||
                                (copy.getScore(Board.otherPlayer(copy.getCurrentPlayer())) >= 25) ||
                                (copy.getNbSeeds() <= 6))
                            this.decision[i] = this.diffScore(copy);
                            /* Sinon, on explore les coups suivants */
                        else {
                            /* Si la profondeur maximale n'est pas atteinte */
                            if (depth < MinMaxNodeAwelicopter.maxDepth) {
                                /* On construit le noeud suivant */
                                MinMaxNodeAwelicopter child = this.getNextNode(copy, depth + 1, alpha, beta);
                                /* On récupère l'évaluation du noeud fils */
                                this.decision[i] = child.getEvaluation();
                            }
                            /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                            else {
                                this.decision[i] = evalSituation(copy, depth) + possedeUnBonCoup(copy) + diffScore(copy);

                                //   this.decision[i] = goodStrategy(copy, i) + evalSituation(copy, depth) + diffScore(copy);
                            }
                        }
                        /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */

                        this.evaluation = this.minmax(this.decision[i], this.evaluation);
                        /* Coupe alpha-beta */
                        if (depth > 0) {
                            if (this.alphabeta(this.evaluation, alpha, beta))
                                break;

                            alpha = this.alpha(this.evaluation, alpha);
                            beta = this.beta(this.evaluation, beta);
                        }
                    } catch (InvalidBotException e) {
                        this.decision[i] = 0;
                    }
                }
            }
    }


    /*
     * renvoie le nombre de cases ou il y a des graines capturables au prochain coup
     * */
    private static final int BOARD_SIZE = Board.NB_HOLES;
    private static final int MIN_SEEDS_FOR_CAPTURE = 2;
    private static final int MAX_SEEDS_FOR_CAPTURE = 3;
    private static final int HALF_BOARD_SIZE = BOARD_SIZE / 2;

    private int possedeUnBonCoup(Board board) {
        int nb_captures = 0;
        int[] playerHoles = board.getPlayerHoles();
        int[] opponentHoles = board.getOpponentHoles();
        for (int i = 0; i < BOARD_SIZE; i++) {
            int player_seeds = playerHoles[i];
            int opponent_index = (i + player_seeds) % BOARD_SIZE;
            int opponent_seeds = opponentHoles[opponent_index];
            int total_seeds = player_seeds + opponent_seeds;
            int range = ( player_seeds + i + ( player_seeds + i ) / 12 - Board.NB_HOLES ) % 12;
            if(range>=HALF_BOARD_SIZE){
                break;
            }
            if (opponent_index >= HALF_BOARD_SIZE || opponent_seeds == 0) {
                continue;
            }
            int target_index = BOARD_SIZE - opponent_index - 1;
            int target_seeds = playerHoles[target_index] + (total_seeds / 12);

            if (target_seeds >= MIN_SEEDS_FOR_CAPTURE && target_seeds <= MAX_SEEDS_FOR_CAPTURE) {
                nb_captures++;
            }
        }
        return nb_captures;
    }


        /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize (Board board, int depth)
    {
        MinMaxNodeAwelicopter.maxDepth = depth + (int)Math.round(((nbGrainesTotale)*1.0)/board.getNbSeeds());;
        MinMaxNodeAwelicopter.player = board.getCurrentPlayer ();
    }

    private int diffScore (Board board)
    {
        return board.getScore (MinMaxNodeAwelicopter.player) - board.getScore (Board.otherPlayer (MinMaxNodeAwelicopter.player));
    }

    /**
     * Mise à jour de alpha
     * @param evaluation L'évaluation courante du noeud
     * @param alpha L'ancienne valeur d'alpha
     * @return
     */
    protected abstract double alpha (double evaluation, double alpha);

    /**
     * Mise à jour de beta
     * @param evaluation L'évaluation courante du noeud
     * @param beta L'ancienne valeur de beta
     * @return
     */
    protected abstract double beta (double evaluation, double beta);

    /**
     * Retourne le min ou la max entre deux valeurs, selon le type de noeud (MinNode ou MaxNode)
     * @param eval1 Un double
     * @param eval2 Un autre double
     * @return Le min ou la max entre deux valeurs, selon le type de noeud
     */
    protected abstract double minmax (double eval1, double eval2);

    /**
     * Indique s'il faut faire une coupe alpha-beta, selon le type de noeud (MinNode ou MaxNode)
     * @param eval L'évaluation courante du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un booléen qui indique s'il faut faire une coupe alpha-beta
     */
    protected abstract boolean alphabeta (double eval, double alpha, double beta);

    /**
     * Retourne un noeud (MinNode ou MaxNode) du niveau suivant
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un noeud (MinNode ou MaxNode) du niveau suivant
     */
    protected abstract MinMaxNodeAwelicopter getNextNode (Board board, int depth, double alpha, double beta);

    /**
     * L'évaluation du noeud
     * @return L'évaluation du noeud
     */
    double getEvaluation ()
    {
        return this.evaluation;
    }



    public static double evalSituation(Board board, int depth) {

        int[] playerHoles = board.getPlayerHoles();
        int[] opponentHoles = board.getOpponentHoles();

        int compteurGraine = 0;
        int compteurKrou = 0;

        boolean isPair = (depth % 2 == 0);

        for(int i = 0; i < Board.NB_HOLES; i++) {
            compteurGraine += (playerHoles[i] < 3) ? (isPair ? 1 : -1) : 0;
            compteurGraine += (opponentHoles[i] < 3) ? (isPair ? -1 : 1) : 0;
            compteurKrou += (playerHoles[i] >= 11) ? 1 : 0;
            compteurKrou -= (opponentHoles[i] >= 11) ? 1 : 0;
        }

        return compteurGraine + compteurKrou;
    }
    /**
     * L'évaluation de chaque coup possible pour le noeud
     * @return
     */
    double [] getDecision ()
    {
        return this.decision;
    }
}
