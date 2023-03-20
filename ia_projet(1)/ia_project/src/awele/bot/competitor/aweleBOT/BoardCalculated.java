package awele.bot.competitor.aweleBOT;

public class BoardCalculated {
    double[] decision;
    int depth;
    int maxDepth;
    BoardCalculated(double[] decision, int depth, int maxDepth){
        this.decision = decision;
        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
    public double[] getDecision() {
        return decision;
    }
    public int getDepth() {
        return depth;
    }
}
