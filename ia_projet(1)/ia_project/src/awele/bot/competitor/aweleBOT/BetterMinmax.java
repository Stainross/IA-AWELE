package awele.bot.competitor.aweleBOT;

import awele.bot.CompetitorBot;

import awele.core.Board;
import awele.core.InvalidBotException;

public class BetterMinmax extends CompetitorBot {
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 6;

    /**
     * @throws InvalidBotException
     */
    public BetterMinmax () throws InvalidBotException
    {
        this.setBotName ("BetterMinMax");
        this.setAuthors("Eliott LALLEMENT","Sofyann SAFSAF");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void finish() {

    }

    @Override
    public double[] getDecision(Board board) {
        MinMaxNode.initialize (board, BetterMinmax.MAX_DEPTH);
        return new MaxNode(board).getDecision ();
    }

    @Override
    public void learn() {

    }
}
