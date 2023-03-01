package awele.bot.competitor.aweleBOT;

import awele.bot.Bot;
import awele.bot.DemoBot;
import awele.bot.demo.first.FirstBot;
import awele.bot.demo.minmax.MinMaxBot;
import awele.bot.random.RandomBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;

import javax.print.attribute.standard.RequestingUserName;
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
    private PartiallyGuidedMinmaxBOT p1 = null;
    private PartiallyGuidedMinmaxBOT p3 = null;
    private ArrayList<double[]> currentNodeDecisions = null;

    private ArrayList<Integer> movesMCTS = new ArrayList<Integer>();
    private ArrayList<Integer> movesOpponent = new ArrayList<Integer>();
    public MCTS() throws InvalidBotException {
        this.setBotName ("AweleBOT- mcts TEST");
        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
        p2 = new PartiallyRandomBOT();
        p4 = new PartiallyRandomBOT();
        p1 = new PartiallyGuidedMinmaxBOT();
        p3 = new PartiallyGuidedMinmaxBOT();
    }

    @Override
    public void initialize() {
        nodes = new ArrayList<Node>();
        ArrayList<Node> childNodes = new ArrayList<>();
        Node root = new Node(null, childNodes);
        nodes.add(root);
        root = expansion(root);
        root = root.parentNode;
        turn = 0;
        isFirstToPlay = false;
    }

    @Override
    public void finish() {
        System.out.println("Nombre de décisions MCTS : " + nbDecisions);
        nbDecisions = 0;
        nodes.clear();

    }

    public void printFirstBranch(Node node){
        System.out.println("Trou n°" + (node.hole+1) + ": " + node.getSimulationWins()+"/"+node.getSimulationCount());
        if(!node.isLeafNode())
            printFirstBranch(node.childNodes.get(0));
    }
    public void printBestBranch(Node node){
        System.out.println("Trou n°" + (node.hole+1) + ": " + node.getSimulationWins()+"/"+node.getSimulationCount());
        if(!node.isLeafNode())
            printFirstBranch(node.getBestFollowingNode());
    }
    public void backpropagation(Node currentNode, double value){
        Node node = currentNode;
        while(node.getParentNode() != null){
            node.increaseSimulationCount();

            node.setSimulationWins(node.simulationWins+(int)value);
            //System.out.println("VALUE :" + node.simulationWins);
            node = node.getParentNode();
        }
    }
    public Node selection(Node currentNode){
        if(currentNode.isLeafNode()) return currentNode;
        return selection(currentNode.getBestFollowingNode());
    }

    public Node expansion(Node currentNode){
        ArrayList<Node> childNodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Node child = new Node(currentNode,null);
            child.hole = i;
            childNodes.add(child);
            nodes.add(child);
        }
        currentNode.childNodes = childNodes;
        //System.out.println(currentNode + " : CREATION DE FILS/ " + currentNode.childNodes);
        return currentNode.childNodes.get(5);
    }
    //Simuler une partie en prenant des actions aléatoires jusqu'à un noeud terminal
    public double simulation(Board board){
        int value = playGame(board);
        return value;
    }

    //Jouer une partie contre un bot random
    //Il faut jouer manuellement les premiers coups donnés par l'arbre
    public int playGame(Board board){
        try{
            ArrayList<Integer> f = (ArrayList<Integer>) Arrays.stream(board.getLog(board.getCurrentPlayer())).boxed().collect(Collectors.toList());
            ArrayList<Integer> f2 = (ArrayList<Integer>) Arrays.stream(board.getLog(Board.otherPlayer(board.getCurrentPlayer()))).boxed().collect(Collectors.toList());
            //if(nbDecisions<40) {
                p2.setFirstMoves(f2);
                p4.setFirstMoves(f);
                p2.learn();
                p4.learn();
                Awele a;
                if(isFirstToPlay) {
                    a = new Awele(p4, p2);
                    a.play();
                    if(a.getWinner()==-1) return 0;
                    return a.getWinner() == 0 ?  1: 0;
                }else {
                    a = new Awele(p2, p4);
                    a.play();
                    if(a.getWinner()==-1) return 0;
                    return a.getWinner() == 0 ?  0: 1;
                }
            /*}
            else{
                p1.setFirstMoves(f2);
                p3.setFirstMoves(f);
                p1.learn();
                p3.learn();
                Awele a;
                if(isFirstToPlay) {
                    a = new Awele(p3, p1);
                    a.play();
                    if(a.getWinner()==-1) return 0;
                    return a.getWinner() == 0 ?  10 + (int)a.getNbMoves(): -10 * (200-(int)a.getNbMoves());
                }else {
                    a = new Awele(p1, p3);
                    a.play();
                    if(a.getWinner()==-1) return 0;
                    return a.getWinner() == 0 ?  -10 * (200-(int)a.getNbMoves()): 10 + (int)a.getNbMoves();
                }
            }*/


        }catch (Exception e){
            System.out.println("erreur instantiation: " + e);
        }
        return 0;
    }

    //Problème: créer 6 noeuds fils mais certaines décisions sont impossibles
    //Que faire ? Créer les 6 noeuds quand même ?
    //Ajouter un paramètre sur chaque noeud correspondant à la case associée ?
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
    public double[] getMCTSDecision(Board board){
        //Récupérer les coups valides
        boolean[] coupsValides = board.validMoves(board.getCurrentPlayer());
        //Récupérer le noeud actuel de l'arbre
        int[] coupsJoués = board.getLog(board.getCurrentPlayer());
        if(nodes.size() == 0) initialize();
        Board copyBoard = (Board) board.clone();
        Node currentNode = nodes.get(0);
        String holesPlayed = "";
        for(int y = 0; y < coupsJoués.length; y++) {
            /* Il faut trouver la valeur qui correspond au trou */
            if(currentNode.childNodes !=null) {
                //System.out.println("enfant:" + currentNode + ": " + currentNode.childNodes);
                holesPlayed += currentNode.childNodes.get(coupsJoués[y]);
                currentNode = currentNode.childNodes.get(coupsJoués[y]);

            }else{
                System.out.println("pas d'enfant:" + currentNode);
                currentNode.printNode();
                System.out.println( currentNode.alreadyVisited() &&  currentNode.isLeafNode());

            }
        }
        for (int i = 0; i < 2000; i++) {

            Node test = selection(currentNode);
            //Node test = selection(nodes.get(0));
            //Une fois le noeud selectionné il faut simuler
            //System.out.println("selected:" + test);
            if (test.alreadyVisited() && test.isLeafNode()) {
                test = expansion(test);
            }

            double value = simulation(board);
            backpropagation(test, value);
        }
        Node abc = currentNode;

        nbDecisions += 1;
        boolean[] coupsPossibles = board.validMoves(board.getCurrentPlayer());
        boolean useMCTS = false;
        for(int i = 0; i < coupsPossibles.length; i++){
            if(coupsPossibles[i] && (double)abc.getChildNodes().get(i).simulationWins/(double)abc.getChildNodes().get(i).simulationCount>0.9)
                useMCTS = true;
        }
        if(useMCTS){
            double[] res = abc.getWinningPercentages();
            System.out.println("MCTS: " + Arrays.toString(res));
            return res;
        }
        MinMaxNode.initialize (board, 6);
        double[] res = new MaxNode(board).getDecision ();
        System.out.println("Minmax: " + Arrays.toString(res) + " | MCTS: " + Arrays.toString(abc.getWinningPercentages()));
        return res;


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
        double[] decision = getMCTSDecision((Board) board.clone());
        return decision;
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
