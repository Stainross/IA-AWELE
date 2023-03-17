package awele.run;

public class GameResult {
    public double[] localPoints;
    public double nbMoves;
    public long runningTime;

    public GameResult(double[] localPoints, double nbMoves, long runningTime) {
        this.localPoints = localPoints;
        this.nbMoves = nbMoves;
        this.runningTime = runningTime;
    }
}