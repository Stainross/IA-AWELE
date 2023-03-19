package awele.bot.competitor.aweleBOT;

import awele.bot.demo.minmax.MinMaxBot;
import awele.core.Awele;
import awele.core.InvalidBotException;

public class Main {
    public static void main (String [] args) {
        try {
            Awele a1 = new Awele(new BetterMinmax(), new MinMaxBot());
            Awele a2 = new Awele(new MinMaxBot(), new BetterMinmax());
            int betterMinMaxWins = 0;
            int minMaxWins = 0;
            int nbMoves = 0;
            for(int i = 0; i < 50; i++){
                a1.play();
                a2.play();
                if(a1.getWinner() == 0) betterMinMaxWins += 1;
                if(a1.getWinner() == 1) minMaxWins += 1;
                if(a2.getWinner() == 1) betterMinMaxWins += 1;
                if(a2.getWinner() == 0) minMaxWins += 1;
                nbMoves += a1.getNbMoves();
                nbMoves += a2.getNbMoves();
                System.out.println(betterMinMaxWins + " : " + minMaxWins);
            }
            double percentage = (double)betterMinMaxWins*100/((double)minMaxWins+(double)betterMinMaxWins);
            System.out.println(percentage + "% de victoires : " + (double)(nbMoves/100) + " coups en moyenne");
        } catch (InvalidBotException e) {
            throw new RuntimeException(e);
        }

    }
}
