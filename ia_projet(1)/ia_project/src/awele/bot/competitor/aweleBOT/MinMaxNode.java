package awele.bot.competitor.aweleBOT;


import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNode
{
    /** Numéro de joueur de l'IA */
    private static int player;

    /** Profondeur maximale */
    private static int maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    private static HashMap<String, double[]> transpositionTable;

    /**
     * Constructeur...
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    public MinMaxNode (Board board, int depth, double alpha, double beta)
    {
        // Check if the game state has already been evaluated
        String stateKey = board.toString();
        //System.out.println(transpositionTable.size());
        if (transpositionTable.containsKey(stateKey)) {
            decision = transpositionTable.get(stateKey);
            //System.out.println("Déjà vu : " + stateKey + " : decision : " + Arrays.toString(decision));
            return;
        }

        // Rest of the Alpha-Beta pruning algorithm


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
                    stateKey = copy.toString();
                    /* Si la nouvelle situation de jeu est un coup qui met fin à la partie,
                       on évalue la situation actuelle */
                    if ((score < 0) ||
                            (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) ||
                            (copy.getNbSeeds () <= 6)) {
                        //this.decision [i] = this.diffScore (copy);
                        this.decision[i] = this.customizedScore(copy);
                        transpositionTable.put(stateKey, decision);
                        //System.out.println("On enregistre : " + stateKey + " decision : " + Arrays.toString(decision));
                        /* Sinon, on explore les coups suivants */
                    }else
                    {
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNode.maxDepth)
                        {
                            /* On construit le noeud suivant */
                            awele.bot.competitor.aweleBOT.MinMaxNode child = this.getNextNode (copy, depth + 1, alpha, beta);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [i] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else {
                            //this.decision [i] = this.diffScore (copy);
                            this.decision[i] = this.customizedScore(copy);
                            transpositionTable.put(stateKey, decision);
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

    /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize (Board board, int maxDepth)
    {
        MinMaxNode.maxDepth = maxDepth;
        MinMaxNode.player = board.getCurrentPlayer ();
        MinMaxNode.transpositionTable = new HashMap<String, double[]>();
    }

    private int diffScore (Board board)
    {
        return board.getScore (MinMaxNode.player) - board.getScore (Board.otherPlayer (MinMaxNode.player));
    }


    private int customizedScore (Board board){
        int myScore = board.getScore(MinMaxNode.player);
        int oppScore = board.getScore(Board.otherPlayer(MinMaxNode.player));

        // Start of game strategy
        int emptyCount = 0;
        int nonEmptyCount = 0;
        int myMobility = 0;
        int oppMobility = 0;
        int myGranaryScore = 0;
        int oppVulnerableCount = 0;

        int mySeedsInHand = board.getPlayerSeeds();
        int oppSeedsInHand = board.getOpponentSeeds();
        for (int i = 0; i < 6; i++) {
            if (board.getPlayerHoles()[i] == 0)
                emptyCount++;
            else if (board.getPlayerHoles()[i] > 2)
                nonEmptyCount++;

            if (board.validMoves(MinMaxNode.player)[i])
                myMobility++;
            if (board.validMoves(Board.otherPlayer(MinMaxNode.player))[i])
                oppMobility++;
            if (board.getPlayerHoles()[i] >= 2 * (6 - i))
                myGranaryScore += board.getPlayerHoles()[i];
            if (board.getOpponentHoles()[i] < 2)
                oppVulnerableCount++;

        }
        int startScore = 10 * (emptyCount - nonEmptyCount) + 2 * (myMobility - oppMobility);

        // Middle of game strategy
        int middleScore = myGranaryScore + 5 * oppVulnerableCount;

        // Bonus for having more seeds in hand
        int seedBonus = 5 * (mySeedsInHand - oppSeedsInHand);

        return 20 * (myScore - oppScore) + startScore + middleScore;
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
    protected abstract MinMaxNode getNextNode (Board board, int depth, double alpha, double beta);

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
