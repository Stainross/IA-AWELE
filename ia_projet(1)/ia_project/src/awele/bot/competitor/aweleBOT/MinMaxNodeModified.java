package awele.bot.competitor.aweleBOT;


import awele.bot.Lucas.Awelicopter;
import awele.core.Board;
import awele.core.InvalidBotException;
import awele.data.AweleData;
import awele.data.AweleObservation;
import javassist.expr.Instanceof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author Alexandre Blansché
 * Noeud d'un arbre MinMax
 */
public abstract class MinMaxNodeModified
{
    private static  ArrayList<AweleObservation> aweleObservations;
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
        if(board.getNbSeeds()<38 && board.getNbSeeds()>18){
            maxDepth = minmaxBetterBot.MAX_DEPTH+2;
        }
       else if(board.getNbSeeds()<18){
            maxDepth = minmaxBetterBot.MAX_DEPTH+4;
        }


        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double [Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = this.worst ();
        Optional<AweleObservation> knownObservation = aweleObservations.stream().filter(obs -> Arrays.equals(obs.getPlayerHoles(), board.getPlayerHoles()) && Arrays.equals(obs.getOppenentHoles(), board.getOpponentHoles())).findFirst();
        int MoveKnown=-1;
        AweleObservation observation = null;
        if (knownObservation.isPresent()) {
             observation = knownObservation.get();
            MoveKnown = observation.getMove();
        }
        /* On parcourt toutes les coups possibles */
        for (int i = 0; i < Board.NB_HOLES; i++) {
            /* Si le coup est jouable */
            if (board.getPlayerHoles()[i] != 0) {
                /* Sélection du coup à jouer */
                double[] decision = new double[Board.NB_HOLES];
                decision[i] = 1;

                if(MoveKnown==i ) {
                    if (observation.isWon()) {
                        if (observation.getPlayerHoles() == board.getPlayerHoles()) {
                                this.decision[i] += 100;
                                break;//it's  a good move so we don't need to check for further move
                        } else {

                                this.decision[i] -= 100;
                                continue;//it's a bad move so we shouldn't play it and we need to check for further move
                        }
                    } else {
                        if (observation.getPlayerHoles() == board.getPlayerHoles()) {
                                this.decision[i] -= 100;
                                continue;//it's a bad move so we shouldn't play it and we need to check for further move

                        } else {
                                this.decision[i] += 100;
                                break;//it's  a good move so we don't need to check for further move

                        }
                    }
                }
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
                        if (depth < MinMaxNodeModified.maxDepth) {
                            /* On construit le noeud suivant */
                            MinMaxNodeModified child = this.getNextNode(copy, depth + 1, alpha, beta);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision[i] = child.getEvaluation();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else {
                            String boardType = BoardType(board);
                            if (boardType.equals("DEBUT")) {
                                this.decision[i] = 3 * this.diffScore(copy) + 7 * earlyGameStrategie(copy) / 10;
                                //this.decision [i] = this.diffScore (copy);
                            } else if (boardType.equals("MILIEU")) {
                                //this.decision [i] = this.diffScore (copy)+vulnerableHoleScore(copy.getPlayerHoles(), copy.getOpponentHoles());
                                this.decision[i] = 3 * this.diffScore(copy) + 7 * earlyGameStrategie(copy) / 10;
                                // this.decision[i] = 4 * this.diffScore(copy) + 6 * middleGameStrategy(copy) / 10;
                            } else if (boardType.equals("FIN")) {
                                this.decision[i] = 4 * this.diffScore(copy) + 6 * lateGameStrategie(copy) / 10;
                            }



                            // Check if the current state exists in the list of known AweleObservations

                           // Optional<AweleObservation> knownObservation = aweleObservations.stream().filter(obs -> Arrays.equals(obs.getPlayerHoles(), board.getPlayerHoles()) && Arrays.equals(obs.getOppenentHoles(), board.getOpponentHoles())).findFirst();
                            /*
                            if (knownObservation.isPresent()) {
                                AweleObservation observation = knownObservation.get();
                                if (observation.getMove() == i)
                                    if (observation.isWon()) {
                                        if (observation.getPlayerHoles() == board.getPlayerHoles()) {
                                            this.decision[i] += 100;
                                        } else {
                                            this.decision[i] -= 100;
                                        }
                                    } else {
                                        if (observation.getPlayerHoles() == board.getPlayerHoles()) {
                                            this.decision[i] -= 100;
                                        } else {
                                            this.decision[i] += 100;
                                        }
                                    }
                            }*/
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


    private int lateGameStrategie(Board board) {
        //generate late game strateegy of the awele game
        int score = 0;
        if(board.getPlayerSeeds()>board.getOpponentSeeds()){
            score = 10;
        }
        else if(board.getPlayerSeeds()<board.getOpponentSeeds()){
            score = -10;
        }
        score+=vulnerableHoleScore(board.getPlayerHoles(), board.getOpponentHoles());
        score-=vulnerableHoleScore(board.getOpponentHoles(), board.getPlayerHoles());
        return score;
    }

    private double middleGameStrategy(Board board) {
        int[] opponentHoles = board.getOpponentHoles();
        int[] playerHoles = board.getPlayerHoles();
        double score = 0;

        // Calculate mobility
        int playerMobility = 0;
        int opponentMobility = 0;
        for (int i = 0; i < playerHoles.length; i++) {
            if (playerHoles[i] > 0) {
                playerMobility++;
            }
            if (opponentHoles[i] > 0) {
                opponentMobility++;
            }
            if (playerHoles[i] > (12-i)) {
                score += 5;
            }
            // Discourage the formation of granaries in the opponent's pits
            if (opponentHoles[i] >(12-i)) {
                score -= 5;
            }
        }
        score += playerMobility - opponentMobility;


        // Add other factors
        score += 2*vulnerableHoleScore(playerHoles, opponentHoles);
        score -= 2*vulnerableHoleScore(opponentHoles, playerHoles);

        return score;
    }


    private int middleGameStrategieSave(Board copy) {
        int[] opponentHoles = copy.getOpponentHoles();
        int[] playerHoles = copy.getPlayerHoles();
        int score = 0;

        int waitingPits = 0;
        for (int i = 0; i < playerHoles.length; i++) {
            // Encourage the formation of granaries in the player's pits
            if (playerHoles[i] > 12) {
                score += 5;
            }
            // Discourage the formation of granaries in the opponent's pits
            if (opponentHoles[i] >12) {
                score -= 5;
            }


            // Encourage having pits with a few seeds for waiting moves
            if (playerHoles[i] > 0 && playerHoles[i] < 3) {
                score += 4;;
            }
        }


        return score;
    }

    private double earlyGameStrategie(Board board) {
        int[] opponentHoles = board.getOpponentHoles();
        int[] playerHoles = board.getPlayerHoles();
        double score =  0;

            for (int i = 1; i < opponentHoles.length; i++) {
                if (opponentHoles[i - 1] > 0 && opponentHoles[i] > 0) {
                    score += 5;
                }
                if (playerHoles[i - 1] > 0 && playerHoles[i] > 0) {
                    score -= 5;
                }
            }
            // Prefer non-consecutive hole sequences
            if (playerHoles[1] > 0 && playerHoles[3] > 0 && playerHoles[5] > 0 && playerHoles[4] == 0 && playerHoles[2] == 0 && playerHoles[0] == 0) {
                score += 4;
            }
            if (playerHoles[4] > 0 && playerHoles[2] > 0 && playerHoles[0] > 0 && playerHoles[1] == 0 && playerHoles[3] == 0 && playerHoles[5] == 0) {
                score += 4;
            }
        score+=vulnerableHoleScore(playerHoles, opponentHoles);
        score-=vulnerableHoleScore(opponentHoles, playerHoles);
        return score;

    }

    public String BoardType(Board board){
        if (board.getNbSeeds() >=32)
            return "DEBUT";
        else if (board.getNbSeeds() <= 8)
            return "FIN";
        else
            return "MILIEU";
    }

    public double vulnerableHoleScore(int[] holes, int[] opponentHoles) {
        int vulnerableHoles = 0;
        int consecutiveVulnerableHoles = 0;
        for (int i = 0; i < holes.length; i++) {
            int seeds = holes[i];
            if(seeds==0)continue;
            int finalPosition = (i + seeds) % 12;

            // If finalPosition is in the opponent's side and results in 2 or 3 seeds
            if (finalPosition >= 6 &&
                    ((opponentHoles[finalPosition - 6] + seeds) % 12 >= 2 && (opponentHoles[finalPosition - 6] + seeds) % 12 <= 3)) {
                consecutiveVulnerableHoles++;
                vulnerableHoles += 3 * consecutiveVulnerableHoles;
            } else {
                consecutiveVulnerableHoles = 0;
            }
        }

        return vulnerableHoles;
    }


    /** Pire score pour un joueur */
    protected abstract double worst ();

    /**
     * Initialisation
     */
    protected static void initialize (Board board, int maxDepth,ArrayList<AweleObservation> aweleObs)
    {
        aweleObservations = aweleObs;
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
