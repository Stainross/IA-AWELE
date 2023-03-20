package awele.bot.competitor.aweleBOT;

import awele.bot.CompetitorBot;
import awele.bot.demo.minmax.MaxNode;
import awele.bot.demo.minmax.MinMaxBot;
import awele.bot.demo.minmax.MinMaxNode;
import awele.bot.demo.random.RandomBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;
import awele.data.AweleData;
import awele.data.AweleObservation;
import java.util.ArrayList;



public class minmaxBetterBot extends CompetitorBot {
    public static  int MAX_DEPTH = 8;
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
    public void learn(){
        AweleData data = AweleData.getInstance ();
        aweleObservations = new ArrayList<>();

        for (AweleObservation observation : data) {
            aweleObservations.add(observation);
        }
    }


    // function that change the depth dynamicly
    public void learnWithDynamicDepth() {
        AweleData data = AweleData.getInstance ();
        aweleObservations = new ArrayList<>();

        for (AweleObservation observation : data) {
            aweleObservations.add(observation);
        }
        RandomBot randomBot = null;
        try
        {
            randomBot = new RandomBot ();
            randomBot.learn ();
        } catch (InvalidBotException e) {

            e.printStackTrace();
            System.exit(0);
        }
        long randomRunningTime = 0;
        int nbMoves = 0;
        if(randomBot!= null) {
            System.out.println("RandomBot");
            for (int i = 0; i < 20; i++) {
                Awele awele = new Awele (randomBot, randomBot);
                try
                {
                    awele.play ();
                }
                catch (InvalidBotException e)
                {
                    e.printStackTrace();
                }
                nbMoves += awele.getNbMoves ();
                randomRunningTime += awele.getRunningTime ();
            }
            long randomAverageDecisionTime = randomRunningTime / nbMoves;

            long averageDecisionTime = 0;
            int nbRunAverage = 5;
            for(int j=0;j<10;j++){
                averageDecisionTime = 0;
                for(int i=0;i<nbRunAverage;i++){
                    System.out.println("RandomBot vs MinMaxBot");
                    Awele awele = new Awele (this, randomBot);
                    try
                    {
                        awele.play ();
                    }
                    catch (InvalidBotException e)
                    {
                        e.printStackTrace();
                    }
                    randomRunningTime += awele.getRunningTime ();
                    long decisionTime = (long) ((2 * awele.getRunningTime ()) / awele.getNbMoves ()) - randomAverageDecisionTime;
                    averageDecisionTime+=decisionTime;
                    System.out.println("Durée d'une prise de décision : " +decisionTime);
                }
                averageDecisionTime/=nbRunAverage;
                if(averageDecisionTime>120){
                    this.MAX_DEPTH = MAX_DEPTH-2;
                    System.out.println("averageDecisionTime trop long: "+averageDecisionTime);
                    break;
                }
                this.MAX_DEPTH = this.MAX_DEPTH+2;
            }
            System.out.println("Max depth final : "+this.MAX_DEPTH);


        }

    }
}
