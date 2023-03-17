package awele.bot.Lucas;

import awele.bot.CompetitorBot;
import awele.bot.demo.random.RandomBot;
import awele.core.Board;
import awele.core.InvalidBotException;


public class Awelicopter extends CompetitorBot {

    public int depth;
    public static int goodDepth;
    public int margeInit = 50;
    public Awelicopter () throws InvalidBotException
    {
        this.setBotName ("Awelicopter");
        this.addAuthor ("Lucas FRANCHINA");
        this.addAuthor ("Tim BRANSTETT");
    }
    public Awelicopter (int depth) throws InvalidBotException
    {
        this.setBotName ("Awelicopter");
        this.addAuthor ("Lucas FRANCHINA");
        this.addAuthor ("Tim BRANSTETT");
        this.depth = depth;
    }



    @Override
    public void initialize()  {}

    @Override
    public void finish() {}

    @Override
    public double[] getDecision(Board board) {
        if(goodDepth!=0){
            MinMaxNodeAwelicopter.initialize (board, goodDepth);
            return new MaxNodeAwelicopter(board).getDecision ();
        } else if (depth != 0) {
            MinMaxNodeAwelicopter.initialize (board, depth);
            return new MaxNodeAwelicopter(board).getDecision ();
        } else {
            MinMaxNodeAwelicopter.initialize (board, 8);
            return new MaxNodeAwelicopter(board).getDecision ();
        }
    }


    private static int extractMilliseconds(final long l) {
        return (int) (l % 1000);
    }

    @Override
    public void learn() {

        RandomBot random = null;
        try
        {
            random = new RandomBot ();
            random.learn ();
        } catch (InvalidBotException e) {

            e.printStackTrace();
            System.exit(0);
        }
        long randomRunningTime = 0;
        int nbMovesRandom = 0;

        for (int k = 0; k < 100; k++)
        {
            CoreLearn aweleRandom = new CoreLearn(random,random);
            try {
                aweleRandom.play ();
            } catch (InvalidBotException e) {
                e.printStackTrace();
            }
            nbMovesRandom += aweleRandom.getNbMoves ();
            randomRunningTime += aweleRandom.getRunningTime ();
        }


        long randomAverageDecisionTime = randomRunningTime/nbMovesRandom;
        for(int i = 6;i<100;i++){
            CoreLearn awele = null;
            try {
                awele = new CoreLearn(new Awelicopter(i), random);
            } catch (InvalidBotException e) {
                e.printStackTrace();
            }
            try {
                assert awele != null;
                awele.play ();

                long decisionTime = (long) ((2 * awele.getRunningTime ()) / awele.getNbMoves ()) - randomAverageDecisionTime;
                String decisionTimeString = String.valueOf(extractMilliseconds(decisionTime));
                System.out.println("Temps de décision : "+decisionTimeString+" ms pour la profondeur "+i);
                if(Integer.parseInt(decisionTimeString)>200-margeInit){
                    goodDepth = i - 2;
                    System.out.println("Awélicopter sera initialisé à la profondeur : "+goodDepth+" avec une marge de sécurité de : "+margeInit+" ms");

                    break;
                }
            } catch (InvalidBotException e) {
                e.printStackTrace();
            }

        }

    }
}
