package awele.bot.competitor.aweleBOT;

import awele.core.Board;

import java.util.ArrayList;

public class Node2 {
    public static final double C = Math.sqrt(2); // Exploration parameter
    public Board board; // The board state represented by this node
    public Node2 parent; // The parent node
    public ArrayList<Node2> children; // The child nodes
    public int visitCount; // Number of times this node has been visited
    public double totalReward; // Total reward obtained from this node
    public boolean expanded; // Whether this node has been fully expanded

    public Node2(Board board, Node2 parent) {
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<Node2>();
        this.visitCount = 0;
        this.totalReward = 0;
        this.expanded = false;
    }

    // Returns the UCB1 score for this node
    public double getUCB1Score() {
        double exploitation = (visitCount == 0) ? 0 : (totalReward / visitCount);
        double exploration = C * Math.sqrt(Math.log(parent.visitCount) / visitCount);
        return exploitation + exploration;
    }
}
