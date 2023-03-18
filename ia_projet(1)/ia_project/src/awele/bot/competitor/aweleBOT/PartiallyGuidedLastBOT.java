//package awele.bot.competitor.aweleBOT;
//
//import awele.bot.DemoBot;
//import awele.core.Board;
//import awele.core.InvalidBotException;
//
//import java.util.ArrayList;
//
//public class PartiallyGuidedLastBOT extends DemoBot {
//    private ArrayList<Integer> firstMoves;
//
//    /**
//     * @throws InvalidBotException
//     */
//    public PartiallyGuidedLastBOT() throws InvalidBotException
//    {
//        this.setBotName ("PartiallyGuidedLastBOT");
//        this.addAuthor ("Alexandre Blansché");
//    }
//
//    /**
//     * Rien à faire
//     */
//    @Override
//    public void initialize ()
//    {
//    }
//
//    /**
//     * Retourne une valeur décroissante avec l'index du trou
//     */
//    @Override
//    public double [] getDecision (Board board)
//    {
//        double [] decision = new double [Board.NB_HOLES];
//        if(firstMoves.size() > 0){
//            firstMoves.get(0);
//            decision = new double[]{0, 0, 0, 0, 0, 0};
//            decision[firstMoves.get(0)] = 1;
//            firstMoves.remove(0);
//            return decision;
//        }
//        for (int i = 0; i < decision.length; i++)
//            decision [i] = i;
//        return decision;
//    }
//    public void setFirstMoves(ArrayList<Integer> f){
//        this.firstMoves = f;
//    }
//    /**
//     * Pas d'apprentissage
//     */
//    @Override
//    public void learn ()
//    {
//    }
//
//    /**
//     * Rien à faire
//     */
//    @Override
//    public void finish ()
//    {
//    }
//}
