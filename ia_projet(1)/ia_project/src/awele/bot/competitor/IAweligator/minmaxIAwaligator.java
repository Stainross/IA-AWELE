package awele.bot.competitor.IAweligator;

import awele.bot.CompetitorBot;
import awele.bot.demo.random.RandomBot;
import awele.core.Awele;
import awele.core.Board;
import awele.core.InvalidBotException;
import awele.data.AweleData;
import awele.data.AweleObservation;
import java.util.ArrayList;



public class minmaxIAwaligator extends CompetitorBot {
    public static  int MAX_DEPTH = 4;
    ArrayList<AweleObservation> aweleObservations;


    public minmaxIAwaligator() throws InvalidBotException
    {
        this.setBotName ("IAwaligator");
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

        learnWithDynamicDepth();//Si le bot dépasse le temps moyen de decisions malgrès cette fonction
        //il faut commenter la ligne ci-dessus et décommenter la ligne ci-dessous
        //this.MAX_DEPTH = 8;
        //ou si ne marche pas encore
        //this.MAX_DEPTH = 6; //pas de valeur impaire
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


            int nbRunAverage = 10;
            for(int j=0;j<10;j++){
                long averageDecisionTime = 0;
                long maxDecisionTime = 0;
                long minDecisionTime = Long.MAX_VALUE;
                for(int i=0;i<nbRunAverage;i++){
                    //System.out.println("RandomBot vs MinMaxBot");
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
                    if(decisionTime>maxDecisionTime)
                        maxDecisionTime = decisionTime;
                    if (decisionTime<minDecisionTime)
                        minDecisionTime = decisionTime;
                    if(decisionTime>220)
                    {
                        System.out.println("averageDecisionTime beaucoup trop long sur une run: "+averageDecisionTime);
                        break;
                    }

                }
                averageDecisionTime/=nbRunAverage;
                System.out.println("maxDecisionTime : "+maxDecisionTime);
                System.out.println("averageDecisionTime : "+averageDecisionTime);
                System.out.println("minDecisionTime : "+minDecisionTime);
                if(averageDecisionTime>180||maxDecisionTime>220){
                    this.MAX_DEPTH = MAX_DEPTH-2;
                    System.out.println("averageDecisionTime trop long: "+averageDecisionTime);
                    break;
                }
                this.MAX_DEPTH = this.MAX_DEPTH+2;

                System.out.println("new max depth : "+this.MAX_DEPTH);
            }
            System.out.println("Max depth final : "+this.MAX_DEPTH);


        }

    }
}
