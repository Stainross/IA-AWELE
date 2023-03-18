package awele.bot.competitor.aweleBOT;

import awele.bot.DemoBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class MCTS2 extends DemoBot {
    private static final int TIME_LIMIT_MS = 100; // Time limit for selecting a move (in milliseconds)

    private Board rootBoard; // The root board of the search tree
    private Node2 rootNode; // The root node of the search tree

    private Node2 currentNode;
    private int nbMovesPlayed;
    private int nbOpponentMovesPlayed;

    private ArrayList<Integer> movesPlayed;
    private ArrayList<Integer> movesPlayedPlayer;
    public MCTS2() throws InvalidBotException {
        this.setBotName ("MCTS VERSION 2");
        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
    }

    @Override
    public void initialize() {
        rootBoard = new Board();
        rootNode = new Node2(rootBoard, null);
        currentNode =  rootNode;
        nbMovesPlayed = 0;
        nbOpponentMovesPlayed = 0;
        movesPlayed = new ArrayList<Integer>();
        movesPlayedPlayer = new ArrayList<Integer>();
    }

    @Override
    public void finish() {
    }

    public void updateCurrentNode(Board board){
        int[] moves = board.getLog(board.getCurrentPlayer());
        int[] opponentMoves = board.getLog(Board.otherPlayer(board.getCurrentPlayer()));
        int nbNewMoves = moves.length - nbMovesPlayed;
        for(int i = moves.length - nbNewMoves; i < moves.length; i++){
            movesPlayed.add(moves[i]);
            movesPlayedPlayer.add(board.getCurrentPlayer());
        }
        int nbNewOpponentMoves = opponentMoves.length - nbOpponentMovesPlayed;
        for(int i = opponentMoves.length - nbNewOpponentMoves; i < opponentMoves.length; i++){
            movesPlayed.add(opponentMoves[i]);
            movesPlayedPlayer.add(Board.otherPlayer(board.getCurrentPlayer()));
        }
        nbMovesPlayed += nbNewMoves;
        nbOpponentMovesPlayed += nbNewOpponentMoves;
        currentNode = rootNode;
        for(int i = 0; i < movesPlayed.size(); i++){
            Integer hole = movesPlayed.get(i);
            boolean foundChild = false;
            if(currentNode.children == null || currentNode.children.size()==0) System.out.println("PAS ENCORE DE FILS");
            for(Node2 childNode : currentNode.children){
                if(childNode.holeIndex == hole){
                    //System.out.println("OUI: " + hole);
                    currentNode = childNode;
                    foundChild = true;
                    break;
                }
            }
            if (!foundChild) {
                // Create a new node for this move and add it to the tree
                double[] decision = {0,0,0,0,0,0};
                decision[hole] = 1;
                Board newBoard = null;
                try {
                    newBoard = currentNode.board.playMoveSimulationBoard(movesPlayedPlayer.get(i),decision);
                } catch (InvalidBotException e) {
                    throw new RuntimeException(e);
                }
                Node2 childNode = new Node2(newBoard, currentNode);
                childNode.holeIndex = hole;
                currentNode.children.add(childNode);
                currentNode.expanded = true;
                currentNode = childNode;
            }
        }
    }
    @Override
    public double[] getDecision(Board board) {
        updateCurrentNode(board);
        long startTime = System.currentTimeMillis(); // Start time of selection
        // Run MCTS simulations until time limit is reached
        while (System.currentTimeMillis() - startTime < TIME_LIMIT_MS) {
            // Selection
            //Node2 selectedNode = select(rootNode);
            Node2 selectedNode = select(currentNode);
            // Expansion
            if (!selectedNode.expanded) {
                expand(selectedNode);
            }
            // Simulation
            double reward = simulate(selectedNode.board);
            // Backpropagation
            backpropagate(selectedNode, reward);
        }
        double[] moveWeights = {0,0,0,0,0,0};
        for (Node2 child : currentNode.children) {
            moveWeights[child.holeIndex] = child.getUCB1Score();
        }
        return moveWeights;
    }

    // Helper function to return random move weights
    private double[] getRandomMoveWeights() {
        double[] moveWeights = new double[6];
        for (int i = 0; i < 6; i++) {
            moveWeights[i] = 1.0 / 6.0;
        }
        return moveWeights;
    }

    // Function to select the most promising node to expand
    private Node2 select(Node2 node) {
        while (node.expanded) {
            if(node.children.size()>0)
                node = getBestChild(node);
        }
        return node;
    }

    // Function to expand a node by adding its children
    private void expand(Node2 node) {
        int[] holeCounts = node.board.getPlayerHoles();
        for (int i = 0; i < 6; i++) {
            if (holeCounts[i] > 0) {
                double[] decision = {0,0,0,0,0,0};
                decision[i] = 1;
                Board newBoard = null;
                try {
                    newBoard = node.board.playMoveSimulationBoard(node.board.getCurrentPlayer(),decision);
                } catch (InvalidBotException e) {
                    throw new RuntimeException(e);
                }
                Node2 childNode = new Node2(newBoard, node);
                childNode.holeIndex = i;
                node.children.add(childNode);
                node.expanded = true;
            }
        }

    }

    // Function to backpropagate the result of a simulation up the tree
    private void backpropagate(Node2 node, double reward) {
        while (node != null) {
            node.visitCount++;
            node.totalReward += reward;
            node = node.parent;
        }
    }

    // Function to choose the child node with the highest UCB1 score
    private Node2 getBestChild(Node2 node) {
        double bestScore = node.children.get(0).getUCB1Score();
        Node2 bestChild = node.children.get(0);
        for (Node2 child : node.children) {
            double score = child.getUCB1Score();
            if (score > bestScore) {
                bestScore = score;
                bestChild = child;
            }
        }
        return bestChild;
    }

    // Function to simulate a game from the given board state
    private double simulate(Board board) {
        Board simBoard = (Board) board.clone();
        Random rand = new Random();
        int nbStagnant = 0;
        boolean end = false;
        while (!end) {
            int[] holeCounts = simBoard.getPlayerHoles();
            int moveIndex = 0;
            ArrayList<Integer> validMoves = new ArrayList<Integer>();
            for (int i = 0; i < 6; i++)
                if (simBoard.validMoves(simBoard.getCurrentPlayer())[i])
                    validMoves.add(i);
            if(validMoves.size()>0)
                moveIndex = validMoves.get(rand.nextInt(validMoves.size()));
            //simBoard.makeMove(moveIndex);
            double[] decision = {0,0,0,0,0,0};
            decision[moveIndex] = 1;
            try {
                simBoard = simBoard.playMoveSimulationBoard(simBoard.getCurrentPlayer(),decision);
                int moveScore = simBoard.playMoveSimulationScore(simBoard.getCurrentPlayer(),decision);
                if (moveScore > 0)
                    nbStagnant = 0;
                else
                    nbStagnant++;
                if ((moveScore < 0) ||
                        (board.getScore (Board.otherPlayer (board.getCurrentPlayer ())) >= 25) ||
                        (board.getNbSeeds () <= 6) ||
                        (nbStagnant >= 1000))
                    end = true;
            } catch (InvalidBotException e) {
                throw new RuntimeException(e);
            }
        }
        return simBoard.getScore(simBoard.getCurrentPlayer()) - simBoard.getScore(Board.otherPlayer(simBoard.getCurrentPlayer()));
    }
    @Override
    public void learn() {

    }
}
