package awele.bot.competitor.aweleBOT;

import awele.bot.CompetitorBot;
import awele.bot.demo.minmax.MaxNode;
import awele.bot.demo.minmax.MinMaxBot;
import awele.bot.demo.minmax.MinMaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.HashMap;

public class minmaxBetterBot extends CompetitorBot {
    private static final int MAX_DEPTH = 7;
    public minmaxBetterBot() throws InvalidBotException
    {
        this.setBotName ("IAWeleBot MinMax");
        this.setAuthors ("Eliott Lallement", "Sofyann Safsaf");
    }
    @Override
    public  void initialize() {

    }

    @Override
    public void finish() {

    }

    @Override
    public double[] getDecision(Board board) {
        MinMaxNodeModified.initialize (board, this.MAX_DEPTH);
        return new MaxNodeModified(board).getDecision ();
    }

    @Override
    public void learn() {

    }
}
