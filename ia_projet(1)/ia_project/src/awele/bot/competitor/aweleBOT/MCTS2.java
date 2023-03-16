package awele.bot.competitor.aweleBOT;

import awele.bot.DemoBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
/*
public class MCTS2 extends DemoBot {

    private static final int SIMULATION_COUNT = 50; // Number of simulations to run for each selection
    private static final int TIME_LIMIT_MS = 100; // Time limit for selecting a move (in milliseconds)

    private Board rootBoard; // The root board of the search tree
    private Node2 rootNode; // The root node of the search tree


    public MCTS2() throws InvalidBotException {
        this.setBotName ("MCTS VERSION 2");
        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
    }

    @Override
    public void initialize() {
        rootBoard = new Board();
        rootNode = new Node2(rootBoard, null);
    }

    @Override
    public void finish() {
        //rootBoard = null;
        //rootNode = null;
    }

    @Override
    public double[] getDecision(Board board) {
        long startTime = System.currentTimeMillis(); // Start time of selection

        // If no moves have been played yet, choose a random move
        /*if (board.getLog(board.getCurrentPlayer()).length == 0) {
            return getRandomMoveWeights();
        }*/

        // Run MCTS simulations until time limit is reached
/*
        while (System.currentTimeMillis() - startTime < TIME_LIMIT_MS) {
            // Selection
            Node2 selectedNode = select(rootNode);
            // Expansion
            if (!selectedNode.expanded) {
                expand(selectedNode);
            }
            // Simulation
            double reward = simulate(selectedNode.board);
            //System.out.println("TEST4:"+reward);
            // Backpropagation
            backpropagate(selectedNode, reward);
        }
        // Choose the best move based on visit counts of child nodes
        int[] holeCounts = rootBoard.getPlayerHoles();
        double[] moveWeights = new double[6];
        double totalVisits = 0;
        for (Node2 child : rootNode.children) {
            //int moveIndex = child.board.getLastMove();
            int[] moves = child.board.getLog(Board.otherPlayer(child.board.getCurrentPlayer()));
            //System.out.println("BOARD:"+child.board.toString());
            //System.out.println("MOVES:"+ Arrays.toString(moves));
            //if(moves.length > 0){
                int moveIndex = moves[moves.length - 1];
                double visits = (double) child.visitCount;
                moveWeights[moveIndex] = visits;
                totalVisits += visits;
            //}
        }
        for (int i = 0; i < 6; i++) {
            moveWeights[i] /= totalVisits;
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
                //newBoard.playMove(i);
                Node2 childNode = new Node2(newBoard, node);
                node.children.add(childNode);
            }
        }
        node.expanded = true;
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
            if (simBoard.getCurrentPlayer() == 0) { // Choose random move for player 1
                ArrayList<Integer> validMoves = new ArrayList<Integer>();
                for (int i = 0; i < 6; i++) {
                    if (simBoard.validMoves(0)[i]) {
                        validMoves.add(i);
                    }
                }
                if(validMoves.size()>0)
                    moveIndex = validMoves.get(rand.nextInt(validMoves.size()));
            } else { // Use greedy strategy for player 2
                ArrayList<Integer> validMoves = new ArrayList<Integer>();
                for (int i = 0; i < 6; i++) {
                    if (simBoard.validMoves(1)[i]) {
                        validMoves.add(i);
                    }
                }
                if(validMoves.size()>0)
                    moveIndex = validMoves.get(rand.nextInt(validMoves.size()));
            }
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
}*/
