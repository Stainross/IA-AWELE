//package awele.bot.competitor.aweleBOT;
//
//import awele.bot.Bot;
//import awele.bot.DemoBot;
//import awele.core.Board;
//import awele.core.InvalidBotException;
//
//public class Random extends DemoBot {
//
//    private java.util.Random random;
//
//    public Random() throws InvalidBotException
//    {
//        this.setBotName ("AweleBOT- random");
//        this.setAuthors("LALLEMENT Eliott","SAFSAF Sofyann");
//    }
//
//    @Override
//    public void initialize() {
//
//    }
//
//    @Override
//    public void finish() {
//
//    }
//
//    @Override
//    public double[] getDecision(Board board) {
//        double [] decision = new double [Board.NB_HOLES];
//        for (int i = 0; i < decision.length; i++)
//            decision [i] = this.random.nextDouble ();
//        return decision;
//    }
//
//    @Override
//    public void learn() {
//        this.random = new java.util.Random(System.currentTimeMillis ());
//    }
//}
