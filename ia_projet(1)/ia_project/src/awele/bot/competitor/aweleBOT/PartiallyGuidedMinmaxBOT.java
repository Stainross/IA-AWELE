package awele.bot.competitor.aweleBOT;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.ArrayList;

public class PartiallyGuidedMinmaxBOT extends DemoBot {
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 6;
    private ArrayList<Integer> firstMoves;
    public PartiallyGuidedMinmaxBOT() throws InvalidBotException
    {
        this.setBotName ("PartiallyGuidedMinmaxBOT");
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
        /*double [] decision = new double [Board.NB_HOLES];
        if(firstMoves.size() > 0){
            firstMoves.get(0);
            decision = new double[]{0, 0, 0, 0, 0, 0};
            decision[firstMoves.get(0)] = 1;
            firstMoves.remove(0);
            return decision;
        }*/
        MinMaxNode.initialize (board, PartiallyGuidedMinmaxBOT.MAX_DEPTH);
        return new MaxNode(board).getDecision ();
    }

    public void setFirstMoves(ArrayList<Integer> f){
        this.firstMoves = f;
    }
    @Override
    public void learn() {
    }
}
