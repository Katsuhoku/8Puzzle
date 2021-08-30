package EightPuzzle.src.model;

public interface EightPuzzleStrategy extends Runnable {

    public EightPuzzleNode[] getRoute();
    public double evaluate(EightPuzzleNode node);
}
