package awele.bot.competitor.aweleBOT;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.ArrayList;
import java.util.Arrays;

public class PartiallyRandomBOT extends DemoBot {

    private java.util.Random random;
    private ArrayList<Integer> firstMoves;
    public PartiallyRandomBOT() throws InvalidBotException
    {
        this.setBotName ("PartiallyRandomBOT");
        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
    }
    @Override
    public void initialize() {

    }

    @Override
    public void finish() {

    }

    @Override
    public double[] getDecision(Board board) {
        double [] decision = new double [Board.NB_HOLES];
        if(firstMoves.size() > 0){
            firstMoves.get(0);
            decision = new double[]{0, 0, 0, 0, 0, 0};
            decision[firstMoves.get(0)] = 1;
            firstMoves.remove(0);
            return decision;
        }
        for (int i = 0; i < decision.length; i++)
            decision [i] = this.random.nextDouble ();
        return decision;
    }

    public void setFirstMoves(ArrayList<Integer> f){
        this.firstMoves = f;
    }
    @Override
    public void learn() {
        this.random = new java.util.Random(System.currentTimeMillis ());
    }
}
