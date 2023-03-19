package awele.bot.competitor.aweleBOT;

import awele.bot.CompetitorBot;
import awele.bot.demo.minmax.MaxNode;
import awele.bot.demo.minmax.MinMaxBot;
import awele.bot.demo.minmax.MinMaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;
import awele.data.AweleData;
import awele.data.AweleObservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class minmaxBetterBot extends CompetitorBot {
    public static final int MAX_DEPTH = 6;
    ArrayList<AweleObservation> aweleObservations;
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
        MinMaxNodeModified.initialize (board, this.MAX_DEPTH, aweleObservations);
        return new MaxNodeModified(board).getDecision ();
    }

    @Override
    public void learn() {
        AweleData data = AweleData.getInstance ();
        aweleObservations = new ArrayList<>();

        for (AweleObservation observation : data) {
            aweleObservations.add(observation);
        }

    }
}
