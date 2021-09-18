package src;

public class AStarAgent extends Thread {
    private static int threadID = 0;

    private int id;
    private AStarStrategy strategy;
    private PuzzleStateNode subroot;
    private SolutionResource solutionResource;

    public AStarAgent(PuzzleStateNode subroot, SolutionResource solutionResource) {
        id = threadID++;
        strategy = new AStarStrategy();

        this.subroot = subroot;
        this.solutionResource = solutionResource;
    }

    @Override
    public void run() {
        PuzzleStateNode solution = strategy.exploreSubtree(subroot);
        if (solution != null) {
            try {
                solutionResource.claimSolution(solution);
            } catch (InterruptedException ie) {
                System.out.println("Thread #" + id + ": Claimed but failed");
            }
        }
    }

    public int getID() {
        return id;
    }
}
