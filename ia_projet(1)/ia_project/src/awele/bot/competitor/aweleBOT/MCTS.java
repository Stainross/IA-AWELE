package awele.bot.competitor.aweleBOT;

import awele.bot.DemoBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class MCTS extends DemoBot {

    private ArrayList<Node> nodes = new ArrayList<Node>();

    private Node currentNode = null;

    private int nbDecisions = 0;

    private Node rootNode = null;

    private int turn = 0;

    private boolean isFirstToPlay = false;

    private PartiallyRandomBOT p2 = null;
    private PartiallyRandomBOT p4 = null;
    private ArrayList<double[]> currentNodeDecisions = null;

    private ArrayList<Integer> movesMCTS = new ArrayList<Integer>();
    private ArrayList<Integer> movesOpponent = new ArrayList<Integer>();
    public MCTS() throws InvalidBotException {
        this.setBotName ("AweleBOT- MCTS VERSION 1");
        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
        p2 = new PartiallyRandomBOT();
        p4 = new PartiallyRandomBOT();
    }

    @Override
    public void initialize() {
        nodes = new ArrayList<Node>();
        ArrayList<Node> childNodes = new ArrayList<>();
        Node root = new Node(null, childNodes);
        nodes.add(root);
        expansion(root, new boolean[]{true, true, true, true, true, true});
        turn = 0;
        isFirstToPlay = false;
    }

    @Override
    public void finish() {

        nodes.clear();

    }

    public void printFirstBranch(Node node){
        System.out.println("Trou n°" + (node.hole) + ": " + node.getSimulationWins()+"/"+node.getSimulationCount());
        if(!node.isLeafNode())
            printFirstBranch(node.childNodes.get(0));
    }
    public void printBestBranch(Node node){
        System.out.println(node+" Trou n°" + (node.hole) + ": " + node.getSimulationWins()+"/"+node.getSimulationCount()+": "+node.childNodes);
        if(!node.isLeafNode())
            printBestBranch(node.getBestFollowingNode());
    }
    public void backpropagation(Node currentNode, double value, Node baseNode){
        currentNode.increaseSimulationCount();
        currentNode.setSimulationWins(currentNode.simulationWins+(int)value);
        if(currentNode != baseNode){
            backpropagation(currentNode.parentNode,value,baseNode);
        }

    }
    public Node selection(Node currentNode){
        if(currentNode.isLeafNode()) return currentNode;
        return selection(currentNode.getBestFollowingNode());
    }

    public Node expansion(Node currentNode, boolean[] validMoves){
        ArrayList<Node> childNodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            if(validMoves[i]){
                Node child = new Node(currentNode,null);
                child.simulationWins = 0;
                child.simulationCount = 0;
                child.hole = i;
                childNodes.add(child);
                nodes.add(child);
            }
        }
        currentNode.childNodes = childNodes;
        return currentNode.childNodes.get(0);
    }
    public double simulation(Board board, Node node){
        int value = playGame(board, node);
        return value;
    }
    public int playGame(Board board, Node node){
        try{
            ArrayList<Integer> f = (ArrayList<Integer>) Arrays.stream(board.getLog(board.getCurrentPlayer())).boxed().collect(Collectors.toList());
            ArrayList<Integer> f2 = (ArrayList<Integer>) Arrays.stream(board.getLog(Board.otherPlayer(board.getCurrentPlayer()))).boxed().collect(Collectors.toList());
                p2.setFirstMoves(f2);
                p4.setFirstMoves(f);
                p2.learn();
                p4.learn();
                Awele a;
                if(isFirstToPlay) {
                    a = new Awele(p4, p2);
                    a.play();
                    if(a.getWinner()==-1) return 1;
                    return a.getWinner() == 0 ?  2: 0;
                }else {
                    a = new Awele(p2, p4);
                    a.play();
                    if(a.getWinner()==-1) return 1;
                    return a.getWinner() == 0 ?  0: 2;
                }
        }catch (Exception e){
            System.out.println("erreur instantiation: " + e);
        }
        return 0;
    }
    public ArrayList<double[]> getDecisionsFromCurrentNode(){
        Node node = this.currentNode;
        ArrayList<double[]> result = new ArrayList<>();
        while(node.getParentNode() != null){
            double[] decision = {0,0,0,0,0,0};
            for(int i = 0; i < 6; i++)
                decision[i] = node.getParentNode().hole == i ? 1 : 0;
            result.add(decision);
            node = node.getParentNode();
        }
        Collections.reverse(result);
        return result;
    }
    public void printDecisionsUntilCurrentNode(Node node){
        int[] moves = new int[15];
        int i = 0;
        while(node.parentNode != null && i < 15){
            moves[i]= node.hole;
            node = node.parentNode;
            i++;
        }
        System.out.println(Arrays.toString(moves));
    }
    public double[] getMCTSDecision(Board board){
        int[] coupsJoués = board.getLog(board.getCurrentPlayer());
        if(nodes.size() == 0) initialize();
        Node currentNode = nodes.get(0);
        for(int y = 0; y < coupsJoués.length; y++) {
            for(int x = 0; x < currentNode.childNodes.size(); x++){
                if(currentNode.childNodes.get(x).hole == coupsJoués[y]){
                    currentNode = currentNode.childNodes.get(x);
                    break;
                }
            }
        }
        for (int i = 0; i < 400; i++) {
            Node test = selection(currentNode);
            if (test.alreadyVisited()) {
                test = expansion(test,board.validMoves(board.getCurrentPlayer()));
            }

            double value = simulation(board, test);
            backpropagation(test, value, currentNode);

        }
        return currentNode.getWinningPercentages();


    }
    public double[] getDecision(Board board){
        if(turn == 0) {
            if (isBoardIntact(board)) {
                isFirstToPlay = true;
            }
            else{
                isFirstToPlay = false;
            }
        }
        turn += 1;
        return getMCTSDecision(board);
    }

    public void clearNodes(){
        for(Node node : nodes){
            node.simulationCount = 0;
            node.simulationWins = 0;
        }
    }
    public boolean isBoardIntact(Board board){
        int[] log0 = board.getLog(0);
        int[] log1 = board.getLog(1);
        if(log0.length > 0 || log1.length > 0)
            return false;
        return true;
    }
    @Override
    public void learn() {

    }

}
