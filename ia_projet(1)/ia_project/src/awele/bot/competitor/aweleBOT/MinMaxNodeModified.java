package awele.bot.competitor.aweleBOT;


import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNodeModified
{
    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    /**
     * Constructeur...
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    public  MinMaxNodeModified(Board board, int depth, double alpha, double beta)
    {
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double [Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = this.worst ();
        /* On parcourt toutes les coups possibles */
        for (int i = 0; i < Board.NB_HOLES; i++)
            /* Si le coup est jouable */
            if (board.getPlayerHoles () [i] != 0)
            {
                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [i] = 1;
                /* On copie la grille de jeu et on joue le coup sur la copie */
                Board copy = (Board) board.clone ();
                try
                {
                    int score = copy.playMoveSimulationScore (copy.getCurrentPlayer (), decision);
                    copy = copy.playMoveSimulationBoard (copy.getCurrentPlayer (), decision);
                    /* Si la nouvelle situation de jeu est un coup qui met fin à la partie,
                       on évalue la situation actuelle */
                    if ((score < 0) ||
                            (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) ||
                            (copy.getNbSeeds () <= 6))
                        this.decision [i] = this.diffScore (copy);
                        /* Sinon, on explore les coups suivants */
                    else
                    {
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNodeModified.maxDepth)
                        {
                            /* On construit le noeud suivant */
                            MinMaxNodeModified child = this.getNextNode (copy, depth + 1, alpha, beta);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [i] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else{
                            // LIGNE A MODIFIER
                            this.decision [i] = this.diffScore (copy)+evaluateBoardForCurrentPlayer(copy);
                        }
                    }
                    /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */
                    this.evaluation = this.minmax (this.decision [i], this.evaluation);
                    /* Coupe alpha-beta */
                    if (depth > 0)
                    {
                        alpha = this.alpha (this.evaluation, alpha);
                        beta = this.beta (this.evaluation, beta);
                    }
                }
                catch (InvalidBotException e)
                {
                    this.decision [i] = 0;
                }
            }
    }
    public String BoardType(Board board){
        if (board.getNbSeeds() >=32)
            return "DEBUT";
        else if (board.getNbSeeds() <= 8)
            return "FIN";
        else
            return "MILIEU";
    }
    public double evaluateBoardForCurrentPlayer(Board board){

        double score = 0;
        if(BoardType(board) == "DEBUT") {
            int[] opponentHoles = board.getOpponentHoles();
            int[] playerHoles = board.getPlayerHoles();
            for (int i = 1; i < opponentHoles.length; i++) {
                if (opponentHoles[i - 1] > 0 && opponentHoles[i] > 0) {
                    score += 1;
                }
            }
            for (int i = 1; i < playerHoles.length; i++) {
                if (playerHoles[i - 1] > 0 && playerHoles[i] > 0) {
                    score -= 1;
                }
            }
        } else if (BoardType(board) == "MILIEU") {/*
            int[] opponentHoles = board.getOpponentHoles();
            int[] playerHoles = board.getPlayerHoles();
            for (int i = 1; i < playerHoles.length; i++) {
                if (playerHoles[i - 1] > 0 && playerHoles[i] > 0) {
                    score -= 1;
                }
            }*/
        } /*else if (BoardType(board) == "FIN") {
            int[] opponentHoles = board.getOpponentHoles();
            int[] playerHoles = board.getPlayerHoles();
            for (int i = 1; i < opponentHoles.length; i++) {
                if (opponentHoles[i - 1] > 0 && opponentHoles[i] > 0) {
                    score += 1;
                }
            }
            for (int i = 1; i < playerHoles.length; i++) {
                if (playerHoles[i - 1] > 0 && playerHoles[i] > 0) {
                    score -= 1;
                }
            }
        }*/


        return score;
    }

    /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize (Board board, int maxDepth)
    {
        MinMaxNodeModified.maxDepth = maxDepth;
        MinMaxNodeModified.player = board.getCurrentPlayer ();
    }

    private int diffScore (Board board)
    {
        return board.getScore (MinMaxNodeModified.player) - board.getScore (Board.otherPlayer (MinMaxNodeModified.player));
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
     *
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta  Le seuil pour la coupe beta
     * @return Un noeud (MinNode ou MaxNode) du niveau suivant
     */
    protected abstract MinMaxNodeModified getNextNode (Board board, int depth, double alpha, double beta);

    /**
     * L'évaluation du noeud
     * @return L'évaluation du noeud
     */
    double getEvaluation ()
    {
        return this.evaluation;
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
